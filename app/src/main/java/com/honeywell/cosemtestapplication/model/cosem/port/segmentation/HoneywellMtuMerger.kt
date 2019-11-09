package com.honeywell.cosemtestapplication.model.cosem.port.segmentation

import com.honeywell.cosemtestapplication.model.cosem.port.TransmitDataMerger
import com.honeywell.cosemtestapplication.model.cosem.port.TransmitException
import no.nordicsemi.android.ble.data.DataStream

class HoneywellMtuMerger : TransmitDataMerger {

    companion object {
        private const val TRANSMIT_ON: Byte = 0x11
        private const val TRANSMIT_OFF: Byte = 0x13
    }

    @Throws(TransmitException::class)
    override fun merge(output: DataStream, lastPacket: ByteArray?, index: Int): Boolean {
        if (lastPacket != null) {
            val header = lastPacket[0]
            val headerValue = HeaderValue.valueOf(header)
            when {
                headerValue === HeaderValue.START -> {
                    output.write(lastPacket, 1, lastPacket.size - 1)
                    return false
                }
                headerValue === HeaderValue.SEQUENCE -> {
                    output.write(lastPacket, 1, lastPacket.size - 1)
                    return false
                }
                headerValue === HeaderValue.END -> {
                    output.write(lastPacket, 1, lastPacket.size - 1)
                    return true
                }
                headerValue === HeaderValue.SINGLE -> {
                    output.write(lastPacket, 1, lastPacket.size - 1)
                    return true
                }
                headerValue === HeaderValue.CONTROL_COMMANDS -> {
                    val transmitByte = lastPacket[1]
                    if (transmitByte == TRANSMIT_ON) {
                        throw TransmitException(TransmitException.TransmitState.ON)
                    } else if (transmitByte == TRANSMIT_OFF) {
                        throw TransmitException(TransmitException.TransmitState.OFF)
                    }
                    return true
                }
            }
        }
        return true
    }

}
