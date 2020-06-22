package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import java.io.IOException
import java.io.Serializable
import java.net.Socket

class ReaderThread<T : ProtoObject>(
    private val socket: Socket,
    private val onObjectReceived: (T) -> Unit,
    private val onError: (IOException) -> Unit = {}
) : Thread() {

    override fun run() {
        val protoDecoder = ProtoDecoder()

        val protoFrameDecoder =
            ProtoFrameDecoder { data ->
                val protoObject = protoDecoder.decode(data)
                @Suppress("UNCHECKED_CAST")
                onObjectReceived(protoObject as T)
            }

        try {
            val input = socket.getInputStream().buffered()
            val buffer = ByteArray(size = 32768)
            while (!isInterrupted) {
                val len = input.read(buffer)
                if (len < 0) {
                    break
                }

                protoFrameDecoder.accept(buffer, len)
            }
        } catch (e: IOException) {
            onError(e)
        } finally {
            socket.closeSafe()
        }
    }
}
