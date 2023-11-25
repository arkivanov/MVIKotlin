package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue.TimeTravelEventValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueParser
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.close

class TimeTravelServer(
    private val controller: TimeTravelController = timeTravelController,
    private val port: Int = DEFAULT_PORT,
    private val onError: (Throwable) -> Unit = {}
) {

    constructor() : this(controller = timeTravelController)

    private var connectionThread: ConnectionThread? = null
    private val clients = HashMap<Int, Client>()

    @MainThread
    fun start() {
        assertOnMainThread()

        if (connectionThread != null) {
            return
        }

        connectionThread = connectionThread().apply { start() }
    }

    @MainThread
    private fun connectionThread(): ConnectionThread =
        ConnectionThread(
            port = port,
            onClientConnected = ::onClientConnected,
            onError = ::onError
        )

    @MainThread
    fun stop() {
        assertOnMainThread()

        if (connectionThread == null) {
            return
        }

        connectionThread?.interrupt()
        connectionThread = null
        clients.values.forEach { it.close() }
        clients.clear()
    }

    private fun onClientConnected(socket: Int) {
        runOnMainThreadIfNotDisposed {
            val reader =
                ReaderThread<TimeTravelCommand>(
                    socket = socket,
                    onRead = { onCommandReceived(command = it, socket = socket) },
                    onDisconnected = { onClientDisconnected(socket) }
                )

            val writer = WriterThread(socket = socket, onDisconnected = { onClientDisconnected(socket) })
            val stateDiff = StateDiff()
            val disposable = controller.states(observer { writer.submit(stateDiff(it)) })

            clients += socket to Client(socket, reader, writer, disposable)

            reader.start()
        }
    }

    private fun onClientDisconnected(socket: Int) {
        runOnMainThreadIfNotDisposed {
            clients.remove(socket)?.close()
        }
    }

    @MainThread
    private fun Client.close() {
        reader.interrupt()
        writer.interrupt()
        disposable.dispose()
        close(socket)
    }

    private fun onCommandReceived(command: TimeTravelCommand, socket: Int) {
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
                is TimeTravelCommand.AnalyzeEvent -> analyzeEvent(eventId = command.eventId, socket = socket)
                is TimeTravelCommand.ExportEvents -> Unit // Not supported
                is TimeTravelCommand.ImportEvents -> Unit // Not supported
            }
        }
    }

    private fun onError(error: Throwable) {
        runOnMainThreadIfNotDisposed {
            onError.invoke(error)
        }
    }

    private fun runOnMainThreadIfNotDisposed(block: () -> Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            if (connectionThread != null) {
                block()
            }
        }
    }

    @MainThread
    private fun analyzeEvent(eventId: Long, socket: Int) {
        val event = controller.state.events.firstOrNull { it.id == eventId } ?: return
        val parsedValue = ValueParser().parseValue(event.value)
        sendData(clientSocket = socket, protoObject = TimeTravelEventValue(eventId = eventId, value = parsedValue))
    }

    @MainThread
    private fun sendData(clientSocket: Int, protoObject: ProtoObject) {
        clients[clientSocket]?.writer?.submit(protoObject)
    }

    private class Client(
        val socket: Int,
        val reader: ReaderThread<*>,
        val writer: WriterThread,
        val disposable: Disposable,
    )
}
