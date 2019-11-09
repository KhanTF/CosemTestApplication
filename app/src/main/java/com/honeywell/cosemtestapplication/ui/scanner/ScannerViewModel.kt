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

    object BluetoothDisabledViewState : ScannerViewModelState()

    data class StartScanUnavailableState(val error: Throwable) : ScannerViewModelState()

}

class ScannerViewModel(private val bluetoothScanner: BluetoothScanner) : ViewModel() {

    private var scannerDisposable: Disposable? = null

    private val scannerViewModelState = MutableLiveData<ScannerViewModelState>()

    fun getScnnerViewModelState(): LiveData<ScannerViewModelState> = scannerViewModelState

    private val devices =
        Collections.synchronizedSortedSet(TreeSet<BluetoothDevice>(Comparator { o1, o2 ->
            o1.name.orEmpty().compareTo(o2.name.orEmpty())
        }))

    fun onStartScan() {
        scannerDisposable = bluetoothScanner
            .startScan()
            .map(ScanResult::getDevice)
            .map(devices::add)
            .filter { it }
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(Throwable::printStackTrace)
            .doOnSubscribe {
                scannerViewModelState.value = ScannerViewModelState.ReceivedScannerListViewState(emptyList())
            }
            .subscribe({
                scannerViewModelState.value = ScannerViewModelState.ReceivedScannerListViewState(devices.toList())
            }, {
                if (it is BluetoothUnavailableException) {
                    scannerViewModelState.value = ScannerViewModelState.BluetoothDisabledViewState
                } else {
                    scannerViewModelState.value = ScannerViewModelState.StartScanUnavailableState(it)
                }
            })
    }

    fun onStopScan() {
        scannerDisposable?.dispose()
        scannerViewModelState.value = ScannerViewModelState.ReceivedScannerListViewState(emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        scannerDisposable?.dispose()
    }

}