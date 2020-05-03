package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable
import java.net.Socket

class ReaderThread<T : Serializable>(
    private val socket: Socket,
    private val onObjectReceived: (T) -> Unit,
    private val onError: (IOException) -> Unit = {}
) : Thread() {

    override fun run() {
        try {
            val input = socket.getInputStream().buffered().let(::ObjectInputStream)
            while (!isInterrupted) {
                onObjectReceived(input.readObject() as T)
            }
        } catch (e: IOException) {
            onError(e)
        } finally {
            socket.closeSafe()
        }
    }
}
