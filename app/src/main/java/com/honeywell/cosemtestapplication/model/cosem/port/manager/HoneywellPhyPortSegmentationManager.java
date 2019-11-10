package com.honeywell.cosemtestapplication.model.cosem.port.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;

import com.honeywell.cosemtestapplication.model.cosem.port.PhyPortBleManagerAndroid;
import com.honeywell.cosemtestapplication.model.cosem.port.TransmitDataMerger;
import com.honeywell.cosemtestapplication.model.cosem.port.TransmitException;
import com.honeywell.cosemtestapplication.model.cosem.port.segmentation.HoneywellMtuMerger;
import com.honeywell.cosemtestapplication.model.cosem.port.segmentation.HoneywellMtuSplitter;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import fr.andrea.libcosemclient.common.MemoryStream;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.data.DataSplitter;
import no.nordicsemi.android.ble.data.DataStream;
import no.nordicsemi.android.ble.exception.BluetoothDisabledException;
import no.nordicsemi.android.ble.exception.DeviceDisconnectedException;
import no.nordicsemi.android.ble.exception.InvalidRequestException;
import no.nordicsemi.android.ble.exception.RequestFailedException;

public class HoneywellPhyPortSegmentationManager extends BleManager<BleManagerCallbacks> implements BleManagerCallbacks, PhyPortBleManagerAndroid {

    private static final UUID HONEYWELL_SERVICE_UUID = UUID.fromString("5f760001-3737-496c-96cd-605337de5252");
    private static final UUID HONEYWELL_TX_CHARACTERISTIC = UUID.fromString("5f768a86-3737-496c-96cd-605337de5252");
    private static final UUID HONEYWELL_RX_CHARACTERISTIC = UUID.fromString("5f76e2c2-3737-496c-96cd-605337de5252");

    private BluetoothGattCharacteristic txCharacteristic;
    private BluetoothGattCharacteristic rxCharacteristic;
    private MemoryStream rxStream = new MemoryStream();

    private final BleManagerGattCallback callback = new BleManagerGattCallback() {
        protected void initialize() {
            HoneywellPhyPortSegmentationManager.this.setNotificationCallback(HoneywellPhyPortSegmentationManager.this.rxCharacteristic).with(HoneywellPhyPortSegmentationManager.this.rxCallback);
            HoneywellPhyPortSegmentationManager.this.enableNotifications(HoneywellPhyPortSegmentationManager.this.rxCharacteristic).enqueue();
        }

        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            BluetoothGattService service = gatt.getService(HoneywellPhyPortSegmentationManager.HONEYWELL_SERVICE_UUID);
            if (service == null) {
                HoneywellPhyPortSegmentationManager.this.log(6, "Not Honeywell UART service!");
                return false;
            } else {
                HoneywellPhyPortSegmentationManager.this.txCharacteristic = service.getCharacteristic(HoneywellPhyPortSegmentationManager.HONEYWELL_TX_CHARACTERISTIC);
                HoneywellPhyPortSegmentationManager.this.rxCharacteristic = service.getCharacteristic(HoneywellPhyPortSegmentationManager.HONEYWELL_RX_CHARACTERISTIC);
                if (HoneywellPhyPortSegmentationManager.this.txCharacteristic != null && HoneywellPhyPortSegmentationManager.this.rxCharacteristic != null) {
                    return true;
                } else {
                    HoneywellPhyPortSegmentationManager.this.log(6, "RX or TX characteristic is null!");
                    return false;
                }
            }
        }

        protected void onDeviceDisconnected() {
            HoneywellPhyPortSegmentationManager.this.log(4, "Device disconnected!");
            HoneywellPhyPortSegmentationManager.this.txCharacteristic = HoneywellPhyPortSegmentationManager.this.rxCharacteristic = null;
        }
    };

    private static final Object MONITOR = new Object();

    private AtomicInteger counter = new AtomicInteger(0);
    private DataSplitter mtuSplitter = new HoneywellMtuSplitter(counter);
    private TransmitDataMerger mtuMerger = new HoneywellMtuMerger();

    private DataStream dataStream = new DataStream();
    private int indexStream = 0;

    private DataReceivedCallback rxCallback = new DataReceivedCallback() {
        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            synchronized (MONITOR) {
                try {
                    if (mtuMerger.merge(dataStream, data.getValue(), indexStream++)) {
                        byte[] bytes = dataStream.toByteArray();
                        rxStream.write(bytes);
                        if (bleDataListener != null) {
                            bleDataListener.onReceived(bytes);
                        }
                        dataStream = new DataStream();
                        indexStream = 0;
                    }
                } catch (TransmitException e) {
                    if (e.getState() == TransmitException.TransmitState.ON) {
                        callback.resumeRequest();
                    } else if (e.getState() == TransmitException.TransmitState.OFF) {
                        callback.pauseRequest();
                    }
                }
            }
        }
    };

    private BleDataListener bleDataListener;

    public HoneywellPhyPortSegmentationManager(@NonNull Context context, BleDataListener bleDataListener) {
        super(context);
        this.setGattCallbacks(this);
        this.bleDataListener = bleDataListener;
    }

    @NonNull
    protected BleManagerGattCallback getGattCallback() {
        return this.callback;
    }

    protected boolean shouldClearCacheWhenDisconnected() {
        return true;
    }

    public void connectSync(BluetoothDevice device) throws InterruptedException, DeviceDisconnectedException, RequestFailedException, InvalidRequestException, BluetoothDisabledException {
        if (!isConnected()) {
            rxStream = new MemoryStream();
            counter.set(0);
        }
        this.connect(device).retry(3, 1000).useAutoConnect(false).await();
    }

    public int write(byte[] data) {
        try {
            this.writeCharacteristic(this.txCharacteristic, data).split(mtuSplitter).await();
            if (bleDataListener != null) {
                bleDataListener.onSend(data);
            }
        } catch (DeviceDisconnectedException | BluetoothDisabledException | InvalidRequestException | RequestFailedException var3) {
            this.log(6, "Failed to send data... " + var3);
            var3.printStackTrace();
            return 0;
        }
        return data.length;
    }

    public byte[] read(int nbytes, int timeoutMs) {
        byte[] buffer = new byte[nbytes];
        int bytes_read = this.rxStream.readWholeBuffer(buffer, timeoutMs);
        if (bytes_read < 0) {
            this.log(6, "Read failed!");
            bytes_read = 0;
        }
        return Arrays.copyOf(buffer, bytes_read);
    }

    @Override
    public void close() {
        super.close();
    }

    public boolean isDataPending(int timeoutMs) {
        return this.rxStream.size() > 0;
    }

    public void onDeviceConnecting(@NonNull BluetoothDevice device) {
    }

    public void onDeviceConnected(@NonNull BluetoothDevice device) {
        counter.set(0);
        indexStream = 0;
    }

    public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
    }

    public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
    }

    public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
    }

    public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
    }

    public void onDeviceReady(@NonNull BluetoothDevice device) {
    }

    public void onBondingRequired(@NonNull BluetoothDevice device) {
    }

    public void onBonded(@NonNull BluetoothDevice device) {
    }

    public void onBondingFailed(@NonNull BluetoothDevice device) {
    }

    public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
    }

    public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
    }
}
