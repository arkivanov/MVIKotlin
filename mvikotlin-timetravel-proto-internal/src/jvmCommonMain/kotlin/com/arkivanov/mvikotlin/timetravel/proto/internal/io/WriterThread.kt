package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import java.io.IOException
import java.net.Socket

class WriterThread(
    private val socket: Socket,
    private val takeObject: () -> ProtoObject,
    private val onError: (IOException) -> Unit = {}
) : Thread() {

    override fun run() {
        try {
            val output = socket.getOutputStream().buffered()
            val frameEncoder = ProtoFrameEncoder { data, size -> output.write(data, 0, size) }
            val protoEncoder = ProtoEncoder(frameEncoder::accept)
            while (!isInterrupted) {
                protoEncoder.encode(takeObject())
                output.flush()
            }
        } catch (e: IOException) {
            onError(e)
        } catch (e: InterruptedException) {
            interrupt()
        } finally {
            socket.closeSafe()
        }
    }
}
