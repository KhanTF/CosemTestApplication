package com.honeywell.cosemtestapplication.model.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber

class BluetoothScannerImpl(private val context: Context) : BluetoothScanner {

    private val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val callbackList: MutableSet<BluetoothScanner.BluetoothScannerCallback> = mutableSetOf()

    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Timber.i("onScanResult : %s", result.device.address)
            for (callback in callbackList) callback.onReceive(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            Timber.i("onBatchScanResults")
            for (result in results) for (callback in callbackList) callback.onReceive(result)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.i("onScanFailed : %d", errorCode)
            for (callback in callbackList) callback.onError(errorCode)
        }

    }

    private fun getBluetoothAdapter(): BluetoothAdapter? = manager.adapter

    override fun startScan(callback: BluetoothScanner.BluetoothScannerCallback): Boolean {
        return if (isBluetoothHardwareAvailable() && isBluetoothEnabled()) {
            if (callbackList.isEmpty()) {
                getBluetoothAdapter()?.bluetoothLeScanner?.startScan(scanCallback)
            }
            callbackList.add(callback)
            true
        } else {
            false
        }
    }

    override fun stopScan(callback: BluetoothScanner.BluetoothScannerCallback) {
        callbackList.remove(callback)
        if (callbackList.isEmpty()) {
            stopScan()
        }
    }

    override fun stopScan() {
        getBluetoothAdapter()?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun isBluetoothEnabled(): Boolean {
        return manager.adapter?.isEnabled ?: false
    }

    private fun isBluetoothHardwareAvailable(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

}