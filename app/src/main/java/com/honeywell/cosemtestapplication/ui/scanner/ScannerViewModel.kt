package com.honeywell.cosemtestapplication.ui.scanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.honeywell.cosemtestapplication.model.scanner.BluetoothScanner
import com.honeywell.cosemtestapplication.ui.base.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ScannerViewModel(private val bluetoothScanner: BluetoothScanner) : BaseViewModel() {

    private val scannerDevicesList = MutableLiveData<List<BluetoothDevice>>()

    private val scannerError = MutableLiveData<Boolean>()

    fun getScannerDevicesList(): LiveData<List<BluetoothDevice>> = scannerDevicesList

    fun getScannerError(): LiveData<Boolean> = scannerError

    private val devices = Collections.synchronizedSortedSet(TreeSet<BluetoothDevice>(Comparator { o1, o2 ->
        o1.name.orEmpty().compareTo(o2.name.orEmpty())
    }))

    private val callback = object : BluetoothScanner.BluetoothScannerCallback {
        override fun onReceive(scanResult: ScanResult) {
            devices.add(scanResult.device)
            scannerDevicesList.value = devices.toList()
        }

        override fun onError(code: Int) {
            scannerError.value = true
            scannerDevicesList.value = emptyList()
        }
    }

    fun onStartScan() {
        scannerDevicesList.value = emptyList()
        scannerError.value =  !bluetoothScanner.startScan(callback)
    }

    fun onStopScan() {
        bluetoothScanner.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothScanner.stopScan()
    }

}