package com.arkivanov.mvikotlin.plugin.idea.timetravel.client

import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.closeSafe
import java.io.IOException
import java.net.Socket
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

internal class ConnectionThread(
    private val host: String,
    private val port: Int,
    private val onConnected: (BlockingQueue<TimeTravelCommand>) -> Unit,
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

        val queue = LinkedBlockingQueue<TimeTravelCommand>()
        onConnected(queue)

        val reader = ReaderThread(socket, onStateUpdateReceived, ::handleError)
        reader.start()
        val writer = WriterThread(socket, queue::take, ::handleError)
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
