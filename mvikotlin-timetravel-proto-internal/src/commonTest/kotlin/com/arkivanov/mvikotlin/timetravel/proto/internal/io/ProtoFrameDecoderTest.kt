package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class ProtoFrameDecoderTest {

    private val separatorPart1 = byteArrayOf(0, 127, 0, -128)
    private val separatorPart2 = byteArrayOf(1, 127, -1, -128)
    private val parts = List(10) { Random.nextBytes(100_000) }
    private val data = parts.reduce { acc, data -> acc + data }

    private val decodedFrames = ArrayList<ByteArray>()

    private val protoFrameDecoder =
        ProtoFrameDecoder { data ->
            decodedFrames += data
        }

    @Test
    fun decodes_first_frame() {
        parts.forEach {
            protoFrameDecoder.accept(it, it.size)
        }
        protoFrameDecoder.accept(separatorPart1, separatorPart1.size)
        protoFrameDecoder.accept(separatorPart2, separatorPart2.size)

        assertDecodedFrame(0)
    }

    @Test
    fun decodes_second_frame() {
        parts.forEach {
            protoFrameDecoder.accept(it, it.size)
        }
        protoFrameDecoder.accept(separatorPart1, separatorPart1.size)
        protoFrameDecoder.accept(separatorPart2, separatorPart2.size)
        decodedFrames.clear()

        parts.forEach {
            protoFrameDecoder.accept(it, it.size)
        }
        protoFrameDecoder.accept(separatorPart1, separatorPart1.size)
        protoFrameDecoder.accept(separatorPart2, separatorPart2.size)

        assertDecodedFrame(0)
    }

    @Test
    fun decodes_three_frames() {
        val compoundData =
            data + separatorPart1 + separatorPart2 +
                data + separatorPart1 + separatorPart2 +
                data + separatorPart1 + separatorPart2

        protoFrameDecoder.accept(compoundData, compoundData.size)

        assertDecodedFrame(0)
        assertDecodedFrame(1)
        assertDecodedFrame(2)
    }

    private fun assertDecodedFrame(index: Int) {
        val frame = decodedFrames[index]
        assertTrue(data.contentEquals(frame.copyOf(data.size)))
    }
}
