package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class WriterThread(
    private val socket: Socket,
    private val takeObject: () -> Any,
    private val onError: (IOException) -> Unit = {}
) : Thread() {

    override fun run() {
        try {
            val output = socket.getOutputStream().buffered().let(::ObjectOutputStream)
            while (!isInterrupted) {
                output.writeObject(takeObject())
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
