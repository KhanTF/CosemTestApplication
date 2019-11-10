package com.honeywell.cosemtestapplication.model.scanner

import android.bluetooth.le.ScanResult
import io.reactivex.Observable

interface BluetoothScanner {

    interface BluetoothScannerCallback{
        fun onReceive(scanResult: ScanResult)
        fun onError(code: Int)
    }

    fun startScan(callback: BluetoothScannerCallback) : Boolean
    fun stopScan()
}