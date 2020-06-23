package com.arkivanov.mvikotlin.plugin.idea.timetravel.client

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.closeSafe
import java.io.IOException
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

internal class ConnectionThread(
    private val host: String,
    private val port: Int,
    private val onConnected: (writer: (ProtoObject) -> Unit) -> Unit,
    private val onStateUpdateReceived: (TimeTravelStateUpdate) -> Unit,
    private val onError: (IOException) -> Unit
) : Thread() {

    private val closeLatch = CountDownLatch(1)
    private val isFailed = AtomicBoolean()

    override fun run() {
        val socket =
            try {
                Socket(host, port)
            } catch (e: IOException) {
                onError(e)
                return
            }

        val reader = ReaderThread(socket = socket, onRead = onStateUpdateReceived, onError = ::handleError)
        val writer = WriterThread(socket = socket, onError = ::handleError)
        onConnected(writer::write)

        reader.start()
        writer.start()

        try {
            closeLatch.await()
        } finally {
            reader.interrupt()
            writer.interrupt()
            socket.closeSafe()
        }
    }

    override fun interrupt() {
        closeLatch.countDown()

        super.interrupt()
    }

    private fun handleError(error: IOException) {
        if (isFailed.compareAndSet(false, true)) {
            onError(error)
        }
    }
}
