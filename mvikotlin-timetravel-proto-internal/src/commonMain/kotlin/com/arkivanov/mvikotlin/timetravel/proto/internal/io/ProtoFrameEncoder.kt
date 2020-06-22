package com.arkivanov.mvikotlin.timetravel.proto.internal.io

class ProtoFrameEncoder(
    private val consume: (data: ByteArray, size: Int) -> Unit
) {

    fun accept(data: ByteArray, size: Int) {
        consume(data, size)
        consume(FRAME_SEPARATOR, FRAME_SEPARATOR.size)
    }
}
