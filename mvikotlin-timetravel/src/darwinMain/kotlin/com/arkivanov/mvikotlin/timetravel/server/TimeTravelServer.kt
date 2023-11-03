package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue.TimeTravelEventValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueParser
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.arkivanov.mvikotlin.utils.internal.assertOnMainThread
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.close

class TimeTravelServer(
    private val controller: TimeTravelController = timeTravelController,
    private val port: Int = DEFAULT_PORT,
    private val onError: (Throwable) -> Unit = {}
) {

    constructor() : this(controller = timeTravelController)

    private var holder: Holder? = Holder()

    @MainThread
    fun start() {
        assertOnMainThread()

        val holder = this.holder ?: return

        val connectionThread = connectionThread()
        holder.connectionThread = connectionThread
        connectionThread.start()
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

        val holder = this.holder ?: return

        this.holder = null
        holder.connectionThread?.interrupt()
        closeClients(holder.clients.values)
    }

    private fun onClientConnected(socket: Int) {
        runOnMainThreadIfNotDisposed { holder ->
            val reader =
                ReaderThread<TimeTravelCommand>(
                    socket = socket,
                    onRead = { onCommandReceived(command = it, socket = socket) },
                    onDisconnected = { onClientDisconnected(socket) }
                )

            val writer = WriterThread(socket = socket, onDisconnected = { onClientDisconnected(socket) })
            val stateDiff = StateDiff()
            val disposable = controller.states(observer { writer.submit(stateDiff(it)) })

            holder.clients += socket to Client(socket, reader, writer, disposable)

            reader.start()
        }
    }

    private fun onClientDisconnected(socket: Int) {
        runOnMainThreadIfNotDisposed { holder ->
            holder.clients.remove(socket)?.also {
                closeClients(listOf(it))
            }
        }
    }

    @MainThread
    private fun closeClients(clients: Iterable<Client>) {
        clients.forEach {
            it.reader.interrupt()
            it.writer.interrupt()
            it.disposable.dispose()
        }

        clients.forEach { close(it.socket) }
    }

    private fun onCommandReceived(command: TimeTravelCommand, socket: Int) {
        runOnMainThreadIfNotDisposed { holder ->
            when (command) {
                is TimeTravelCommand.StartRecording -> controller.startRecording()
                is TimeTravelCommand.StopRecording -> controller.stopRecording()
                is TimeTravelCommand.MoveToStart -> controller.moveToStart()
                is TimeTravelCommand.StepBackward -> controller.stepBackward()
                is TimeTravelCommand.StepForward -> controller.stepForward()
                is TimeTravelCommand.MoveToEnd -> controller.moveToEnd()
                is TimeTravelCommand.Cancel -> controller.cancel()
                is TimeTravelCommand.DebugEvent -> controller.debugEvent(eventId = command.eventId)
                is TimeTravelCommand.AnalyzeEvent -> analyzeEvent(eventId = command.eventId, holder = holder, socket = socket)
                is TimeTravelCommand.ExportEvents -> Unit // Not supported
                is TimeTravelCommand.ImportEvents -> Unit // Not supported
            }
        }
    }

    private fun onError(error: Throwable) {
        runOnMainThreadIfNotDisposed { _ ->
            onError.invoke(error)
        }
    }

    private fun runOnMainThreadIfNotDisposed(block: (Holder) -> Unit) {
        val callback: () -> Unit = { holder?.also(block) }

        dispatch_async(dispatch_get_main_queue(), callback)
    }

    @MainThread
    private fun analyzeEvent(eventId: Long, holder: Holder, socket: Int) {
        val event = controller.state.events.firstOrNull { it.id == eventId } ?: return
        val parsedValue = ValueParser().parseValue(event.value)
        holder.sendData(clientSocket = socket, protoObject = TimeTravelEventValue(eventId = eventId, value = parsedValue))
    }

    @MainThread
    private fun Holder.sendData(clientSocket: Int, protoObject: ProtoObject) {
        clients[clientSocket]?.writer?.submit(protoObject)
    }

    private class Holder {
        var connectionThread: ConnectionThread? = null
        val clients: MutableMap<Int, Client> = HashMap()
    }

    private class Client(
        val socket: Int,
        val reader: ReaderThread<*>,
        val writer: WriterThread,
        val disposable: Disposable
    )
}
