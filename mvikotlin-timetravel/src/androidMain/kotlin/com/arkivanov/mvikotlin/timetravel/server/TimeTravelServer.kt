package com.arkivanov.mvikotlin.timetravel.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.closeSafe
import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread

class TimeTravelServer(
    private val controller: TimeTravelController = timeTravelController,
    private val port: Int = DEFAULT_PORT,
    private val onError: (IOException) -> Unit = {}
) {

    private val handler = Handler(Looper.getMainLooper())
    private var connectionThread: ConnectionThread? = null
    private var clients: Map<Socket, Client> = emptyMap()

    fun start() {
        if (connectionThread != null) {
            return
        }

        val connectionThread =
            ConnectionThread(
                port = port,
                onClientConnected = ::onClientConnected,
                onError = ::onError
            )

        this.connectionThread = connectionThread
        connectionThread.start()
    }

    fun stop() {
        connectionThread?.interrupt()
        connectionThread = null
        closeClients(clients.values)
        clients = emptyMap()
    }

    private fun onClientConnected(socket: Socket) {
        runOnMainThreadIfNotDisposed {
            val reader =
                ReaderThread(
                    socket = socket,
                    onRead = ::onCommandReceived,
                    onDisconnected = { onClientDisconnected(socket) }
                )

            val writer = WriterThread(socket = socket, onDisconnected = { onClientDisconnected(socket) })
            val stateDiff = StateDiff()
            val disposable = controller.states(observer { writer.write(stateDiff(it)) })

            clients = clients + (socket to Client(socket, reader, writer, disposable))

            reader.start()
            writer.start()
        }
    }

    private fun onClientDisconnected(socket: Socket) {
        runOnMainThreadIfNotDisposed {
            clients[socket]?.also {
                closeClients(listOf(it))
            }
            clients = clients - socket
        }
    }

    private fun closeClients(clients: Iterable<Client>) {
        clients.forEach {
            it.reader.interrupt()
            it.writer.interrupt()
            it.disposable.dispose()
        }

        thread {
            clients.forEach {
                it.socket.closeSafe()
            }
        }
    }

    private fun onCommandReceived(command: TimeTravelCommand) {
        runOnMainThreadIfNotDisposed {
            when (command) {
                is TimeTravelCommand.StartRecording -> controller.startRecording()
                is TimeTravelCommand.StopRecording -> controller.stopRecording()
                is TimeTravelCommand.MoveToStart -> controller.moveToStart()
                is TimeTravelCommand.StepBackward -> controller.stepBackward()
                is TimeTravelCommand.StepForward -> controller.stepForward()
                is TimeTravelCommand.MoveToEnd -> controller.moveToEnd()
                is TimeTravelCommand.Cancel -> controller.cancel()
                is TimeTravelCommand.DebugEvent -> controller.debugEvent(eventId = command.eventId)
            }.let {}
        }
    }

    private fun onError(error: IOException) {
        runOnMainThreadIfNotDisposed {
            Log.e("MviKotlin", "TimeTravelServer error: $error")
            onError.invoke(error)
        }
    }

    private inline fun runOnMainThreadIfNotDisposed(crossinline block: () -> Unit) {
        handler.post {
            if (connectionThread != null) {
                block()
            }
        }
    }

    private class Client(
        val socket: Socket,
        val reader: ReaderThread<*>,
        val writer: WriterThread,
        val disposable: Disposable
    )
}
