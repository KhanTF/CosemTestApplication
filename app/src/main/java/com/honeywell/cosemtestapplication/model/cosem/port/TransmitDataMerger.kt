package com.honeywell.cosemtestapplication.model.cosem.port

import androidx.annotation.IntRange
import no.nordicsemi.android.ble.data.DataStream

interface TransmitDataMerger {
    @Throws(TransmitException::class)
    fun merge(output: DataStream, lastPacket: ByteArray?, @IntRange(from = 0) index: Int): Boolean
}
