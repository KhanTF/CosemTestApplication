package com.honeywell.cosemtestapplication.model.scanner

import android.bluetooth.le.ScanResult
import io.reactivex.Observable

interface BluetoothScanner {
    fun startScan(): Observable<ScanResult>
}