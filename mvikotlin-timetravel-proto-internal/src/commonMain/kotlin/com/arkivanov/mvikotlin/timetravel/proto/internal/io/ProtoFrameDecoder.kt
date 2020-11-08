package com.arkivanov.mvikotlin.timetravel.proto.internal.io

class ProtoFrameDecoder(
    private val consume: (ByteArray) -> Unit
) {

    private var buffer = ByteArray(size = 1024)
    private var index = 0

    fun accept(data: ByteArray, size: Int) {
        for (i in 0 until size) {
            ensureFreeSpace()
            buffer[index] = data[i]
            index++
            if (isFullFrame()) {
                consume(buffer)
                index = 0
            }
        }
    }

    private fun isFullFrame(): Boolean {
        if (index < FRAME_SEPARATOR.size) {
            return false
        }

        for (i in FRAME_SEPARATOR.indices) {
            if (buffer[index - FRAME_SEPARATOR.size + i] != FRAME_SEPARATOR[i]) {
                return false
            }
        }

        return true
    }

    private fun ensureFreeSpace() {
        if (index >= buffer.size) {
            buffer = buffer.copyOf(buffer.size * 2)
        }
    }
}
