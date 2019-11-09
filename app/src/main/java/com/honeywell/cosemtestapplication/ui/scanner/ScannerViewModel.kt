package com.honeywell.cosemtestapplication.ui.scanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.honeywell.cosemtestapplication.model.scanner.BluetoothUnavailableException
import com.honeywell.cosemtestapplication.model.scanner.BluetoothScanner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

sealed class ScannerViewModelState {

    data class ReceivedScannerListViewState(val devises: List<BluetoothDevice>) : ScannerViewModelState()

    object StartScanUnavailableState : ScannerViewModelState()

}

class ScannerViewModel(private val bluetoothScanner: BluetoothScanner) : ViewModel() {

    private val scannerViewModelState = MutableLiveData<ScannerViewModelState>()

    fun getScannerViewModelState(): LiveData<ScannerViewModelState> = scannerViewModelState

    private val devices = Collections.synchronizedSortedSet(TreeSet<BluetoothDevice>(Comparator { o1, o2 ->
        o1.name.orEmpty().compareTo(o2.name.orEmpty())
    }))

    private val callback = object : BluetoothScanner.BluetoothScannerCallback {
        override fun onReceive(scanResult: ScanResult) {
            devices.add(scanResult.device)
            scannerViewModelState.value = ScannerViewModelState.ReceivedScannerListViewState(devices.toList())
        }

        override fun onError(code: Int) {
            scannerViewModelState.value = ScannerViewModelState.StartScanUnavailableState
        }
    }

    fun onStartScan() {
        if (!bluetoothScanner.startScan(callback)) {
            scannerViewModelState.value = ScannerViewModelState.StartScanUnavailableState
        }else{
            scannerViewModelState.value = ScannerViewModelState.ReceivedScannerListViewState(emptyList())
        }
    }

    fun onStopScan() {
        bluetoothScanner.stopScan(callback)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothScanner.stopScan()
    }

}