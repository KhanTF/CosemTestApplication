package com.honeywell.cosemtestapplication.model.cosem

import android.bluetooth.BluetoothDevice

interface CosemManager {
    fun connect(device: BluetoothDevice, auth: Auth)
    fun disconnect()
    fun isConnect(): Boolean
    fun <T> execute(f: CosemWrapper.() -> T): T
}