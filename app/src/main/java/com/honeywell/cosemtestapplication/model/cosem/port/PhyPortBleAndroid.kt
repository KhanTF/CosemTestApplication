package com.honeywell.cosemtestapplication.model.cosem.port

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.honeywell.cosemtestapplication.model.cosem.port.manager.HoneywellPhyPortSegmentationManager
import fr.andrea.libcosemclient.port.JPhyPort
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import no.nordicsemi.android.ble.exception.DeviceDisconnectedException
import no.nordicsemi.android.ble.exception.InvalidRequestException
import no.nordicsemi.android.ble.exception.RequestFailedException
import java.io.Closeable

class PhyPortBleAndroid(private val provider: (Context) -> PhyPortBleManagerAndroid = { HoneywellPhyPortSegmentationManager(it) }) : JPhyPort(), Closeable {

    private var manager: PhyPortBleManagerAndroid? = null

    @Throws(DeviceDisconnectedException::class, RequestFailedException::class, InvalidRequestException::class, BluetoothDisabledException::class, InterruptedException::class)
    fun connect(device: BluetoothDevice, context: Context) {
        this.manager = provider(context).apply {
            connectSync(device)
        }
    }

    override fun close() {
        this.manager?.close()
    }

    override fun write(buffer: ByteArray): Int {
        return this.manager!!.write(buffer)
    }

    override fun read(nbytes: Int, timeoutMs: Int): ByteArray {
        return this.manager!!.read(nbytes, timeoutMs)
    }

    override fun isDataPending(timeoutMs: Int): Boolean {
        return this.manager!!.isDataPending(timeoutMs)
    }

    override fun isConnectedProtocol(): Boolean {
        return true
    }

    override fun isConnected(): Boolean {
        return this.manager!!.isConnected
    }

    override fun isStream(): Boolean {
        return true
    }

    override fun getVersion(): String {
        return "PhyPortBLEAndroid-ANDROID-" + this.javaClass.simpleName
    }

    override fun setLogPath(tracePath: String) {
        //TODO NOTHING
    }

}
