package com.honeywell.cosemtestapplication.model.cosem.port.segmentation

import kotlin.experimental.and
import kotlin.experimental.or

enum class HeaderValue(val bits: Byte, val isTerminate: Boolean) {
    START(0b00000001.toByte(), false),
    SEQUENCE( 0b00000100.toByte(), false),
    END(0b00000010.toByte(), true),
    SINGLE(0b00000011.toByte(), true),
    CONTROL_COMMANDS(0b00000000.toByte(), true);

    fun getHeaderByte(sequenceNumber: Int): Byte {
        var number = sequenceNumber % 16
        if (number > 15) {
            number = 0
        }
        return (bits or (number shl 4).toByte())
    }

    companion object {

        private const val INDICATION_MASK = 0b00000111.toByte()
        private const val SEQUENCE_MASK = 0b11110000.toByte()

        fun valueOf(header: Byte): HeaderValue {
            val indication = (header and INDICATION_MASK)
            return if (indication == START.bits) {
                START
            } else if (indication == SEQUENCE.bits) {
                SEQUENCE
            } else if (indication == END.bits) {
                END
            } else if (indication == SINGLE.bits) {
                SINGLE
            } else if (indication == CONTROL_COMMANDS.bits) {
                CONTROL_COMMANDS
            } else {
                throw IllegalArgumentException("Incorrect header indication \$indication")
            }
        }

        fun getSequenceNumber(b: Byte): Int {
            return (b and SEQUENCE_MASK).toInt()
        }
    }

}
