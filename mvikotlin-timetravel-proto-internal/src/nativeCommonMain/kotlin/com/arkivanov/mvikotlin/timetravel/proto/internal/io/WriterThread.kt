package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.thread.LooperThread
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.value
import platform.posix.close
import platform.posix.write

class WriterThread(
    private val socket: Int,
    private val onDisconnected: () -> Unit = {},
    private val onError: (Throwable) -> Unit = {}
) : LooperThread<ProtoObject>() {

    override fun run(message: ProtoObject) {
        try {
            getEncoder().encode(message)
        } catch (e: Throwable) {
            onError(e)
            close(socket)
            interrupt()
            onDisconnected()
        }
    }

    private fun getEncoder(): ProtoEncoder {
        val frameEncoder =
            ProtoFrameEncoder { data, size ->
                memScoped {
                    val buff = allocArray<ByteVar>(size) { value = data[it] }
                    write(socket, buff, size.convert())
                }
            }

        return ProtoEncoder(frameEncoder::accept)
    }
}