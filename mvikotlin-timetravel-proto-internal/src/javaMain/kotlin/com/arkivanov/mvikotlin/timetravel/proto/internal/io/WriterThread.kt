package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class WriterThread(
    private val socket: Socket,
    private val onDisconnected: () -> Unit = {},
    private val onError: (Exception) -> Unit = {}
) : Thread() {

    private val queue = LinkedBlockingQueue<ProtoObject>()

    override fun run() {
        try {
            val output = socket.getOutputStream().buffered()
            val frameEncoder = ProtoFrameEncoder { data, size -> output.write(data, 0, size) }
            val protoEncoder = ProtoEncoder(frameEncoder::accept)
            while (!isInterrupted) {
                protoEncoder.encode(queue.take())
                output.flush()
            }
        } catch (e: InterruptedException) {
            interrupt()
        } catch (e: Exception) {
            onError(e)
        } finally {
            socket.closeSafe()
            onDisconnected()
        }
    }

    fun write(protoObject: ProtoObject) {
        queue += protoObject
    }
}
