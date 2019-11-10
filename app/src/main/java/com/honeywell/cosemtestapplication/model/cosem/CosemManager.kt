package com.honeywell.cosemtestapplication.model.cosem

import android.bluetooth.BluetoothDevice
import com.honeywell.cosemtestapplication.model.cosem.port.manager.BleDataListener

interface CosemManager {
    fun setLogPath(path: String)
    fun connect(device: BluetoothDevice, dataListener: BleDataListener)
    fun disconnect()
    fun isConnect(): Boolean
    fun <T> execute(f: CosemWrapper.() -> T): T
}