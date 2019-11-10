package com.honeywell.cosemtestapplication.model.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber
import java.util.concurrent.Callable
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class BluetoothScannerImpl(
    private val context: Context
) : BluetoothScanner {

    private val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private var callback: BluetoothScanner.BluetoothScannerCallback? = null

    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Timber.i("onScanResult : %s", result.device.address)
            callback?.onReceive(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            Timber.i("onBatchScanResults")
            for (result in results) callback?.onReceive(result)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.i("onScanFailed : %d", errorCode)
            callback?.onError(errorCode)
        }

    }

    private fun getBluetoothAdapter(): BluetoothAdapter? = manager.adapter

    override fun startScan(callback: BluetoothScanner.BluetoothScannerCallback): Boolean {
        return if (isBluetoothHardwareAvailable() && isBluetoothEnabled()) {
            this.callback = callback
            getBluetoothAdapter()?.bluetoothLeScanner?.startScan(scanCallback)
            true
        } else {
            false
        }
    }

    override fun stopScan() {
        this.callback = null
        stopScanInternal()
    }

    private fun stopScanInternal() {
        getBluetoothAdapter()?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun isBluetoothEnabled(): Boolean {
        return manager.adapter?.isEnabled ?: false
    }

    private fun isBluetoothHardwareAvailable(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

}