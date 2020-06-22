package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.thread.Thread
import com.badoo.reaktive.utils.printStack
import kotlinx.cinterop.Arena
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.readBytes
import platform.posix.close
import platform.posix.read

class ReaderThread<T : ProtoObject>(
    private val socket: Int,
    private val onRead: (T) -> Unit,
    private val onDisconnected: () -> Unit = {},
    private val onError: (Throwable) -> Unit = {}
) : Thread() {

    override fun run() {
        val protoDecoder = ProtoDecoder()

        val protoFrameDecoder =
            ProtoFrameDecoder { data ->
                val protoObject = protoDecoder.decode(data)
                @Suppress("UNCHECKED_CAST")
                onRead(protoObject as T)
            }

        val arena = Arena()

        try {
            val buffer = arena.allocArray<ByteVar>(length = BUFFER_SIZE)
            while (!isInterrupted) {
                val len: Int = read(socket, buffer, BUFFER_SIZE.convert()).convert()
                if (len <= 0) {
                    break
                }

                protoFrameDecoder.accept(buffer.readBytes(len), len)
            }
        } catch (e: Throwable) {
            onError(e)
        } finally {
            arena.clear()
            close(socket)
            onDisconnected()
        }
    }

    private companion object {
        private const val BUFFER_SIZE = 32768
    }
}