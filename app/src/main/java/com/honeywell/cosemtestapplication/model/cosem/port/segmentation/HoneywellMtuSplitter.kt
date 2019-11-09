package com.honeywell.cosemtestapplication.model.cosem.port.segmentation

import no.nordicsemi.android.ble.data.DataSplitter
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class HoneywellMtuSplitter(private val counter: AtomicInteger) : DataSplitter {

    private fun getCountChunk(message: ByteArray, maxLength: Int): Int {
        return Math.ceil(message.size.toDouble() / (maxLength - 1).toDouble()).toInt()
    }

    override fun chunk(message: ByteArray, index: Int, maxLength: Int): ByteArray? {
        val countChunk = getCountChunk(message, maxLength)
        if (index >= countChunk) return null

        val start = index * (maxLength - 1)
        var contentLength = maxLength - 1
        if (start + contentLength >= message.size) {
            contentLength = message.size - start
        }
        val chunk = ByteArray(contentLength + 1)

        System.arraycopy(message, start, chunk, 1, contentLength)
        chunk[0] = getHeader(index, countChunk).getHeaderByte(counter.getAndIncrement())
        return chunk.also {
            Timber.d(it.map { b -> String.format("%02x", b) }.joinToString(", "))
        }
    }

    private fun getHeader(i: Int, countChunk: Int): HeaderValue {
        return if (countChunk == 1) {
            HeaderValue.SINGLE
        } else if (i == 0) {
            HeaderValue.START
        } else if (i == countChunk - 1) {
            HeaderValue.END
        } else if (i > 0 && i < countChunk - 1) {
            HeaderValue.SEQUENCE
        } else
            throw IndexOutOfBoundsException("i must be > 0 and < $countChunk")
    }

}
