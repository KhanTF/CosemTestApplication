package com.honeywell.cosemtestapplication.model.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class BluetoothScannerImpl(private val context: Context) : BluetoothScanner {

    private val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private fun getScanCallback(subject: PublishSubject<ScanResult>) = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Timber.i("onScanResult : %s", result.device.address)
            subject.onNext(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            Timber.i("onBatchScanResults")
            for (result in results) subject.onNext(result)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.i("onScanFailed : %d", errorCode)
            subject.onError(Exception("Scan failed"))
        }

    }

    private fun getSafeAdapter(): BluetoothAdapter {
        return if (isBluetoothHardwareAvailable() && isBluetoothEnabled()) {
            manager.adapter
        } else {
            throw BluetoothUnavailableException("BLE not supported or disabled")
        }
    }

    override fun startScan(): Observable<ScanResult> {
        val subject = PublishSubject.create<ScanResult>()
        val callback = getScanCallback(subject)
        return Completable
            .fromAction {
                getSafeAdapter().bluetoothLeScanner.startScan(callback)
            }
            .andThen(subject)
            .doAfterTerminate {
                stopScan(callback)
            }
            .doOnDispose {
                stopScan(callback)
            }
    }

    private fun stopScan(callback: ScanCallback) {
        getSafeAdapter().bluetoothLeScanner.stopScan(callback)
    }

    private fun isBluetoothEnabled(): Boolean {
        return manager.adapter?.isEnabled ?: false
    }

    private fun isBluetoothHardwareAvailable(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

}