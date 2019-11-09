package com.honeywell.cosemtestapplication.model.cosem.port

import android.bluetooth.BluetoothDevice

import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import no.nordicsemi.android.ble.exception.DeviceDisconnectedException
import no.nordicsemi.android.ble.exception.InvalidRequestException
import no.nordicsemi.android.ble.exception.RequestFailedException

interface PhyPortBleManagerAndroid {

    val isConnected: Boolean
    @Throws(
        DeviceDisconnectedException::class,
        RequestFailedException::class,
        InvalidRequestException::class,
        BluetoothDisabledException::class,
        InterruptedException::class
    )
    fun connectSync(device: BluetoothDevice)

    fun write(var1: ByteArray): Int

    fun read(var1: Int, var2: Int): ByteArray

    fun isDataPending(var1: Int): Boolean

    fun close()
}
