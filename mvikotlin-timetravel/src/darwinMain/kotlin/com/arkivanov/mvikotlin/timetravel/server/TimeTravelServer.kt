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
import com.arkivanov.mvikotlin.utils.internal.AtomicRef
import com.arkivanov.mvikotlin.utils.internal.IsolatedRef
import com.arkivanov.mvikotlin.utils.internal.assertOnMainThread
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getAndUpdate
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.close
import kotlin.native.concurrent.freeze

class TimeTravelServer(
    controller: TimeTravelController = timeTravelController,
    private val port: Int = DEFAULT_PORT,
    onError: (Throwable) -> Unit = {}
) {

    constructor() : this(controller = timeTravelController)

    init {
        assertOnMainThread()
    }

    private val holderRef: AtomicRef<IsolatedRef<Holder>?> = atomic(IsolatedRef(Holder(controller = controller, onError = onError)))

    @MainThread
    fun start() {
        assertOnMainThread()

        val holder = getHolder() ?: return

        val connectionThread = connectionThread()
        holder.connectionThread = connectionThread
        connectionThread.start()
    }

    private fun connectionThread(): ConnectionThread =
        ConnectionThread(
            port = port,
            onClientConnected = ::onClientConnected,
            onError = ::onError
        )

    @MainThread
    fun stop() {
        assertOnMainThread()

        val holder = removeHolder() ?: return

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
            val disposable = holder.controller.states(observer { writer.submit(stateDiff(it)) })

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
                is TimeTravelCommand.StartRecording -> holder.controller.startRecording()
                is TimeTravelCommand.StopRecording -> holder.controller.stopRecording()
                is TimeTravelCommand.MoveToStart -> holder.controller.moveToStart()
                is TimeTravelCommand.StepBackward -> holder.controller.stepBackward()
                is TimeTravelCommand.StepForward -> holder.controller.stepForward()
                is TimeTravelCommand.MoveToEnd -> holder.controller.moveToEnd()
                is TimeTravelCommand.Cancel -> holder.controller.cancel()
                is TimeTravelCommand.DebugEvent -> holder.controller.debugEvent(eventId = command.eventId)
                is TimeTravelCommand.AnalyzeEvent -> analyzeEvent(eventId = command.eventId, holder = holder, socket = socket)
                is TimeTravelCommand.ExportEvents -> Unit // Not supported
                is TimeTravelCommand.ImportEvents -> Unit // Not supported
            }.let {}
        }
    }

    private fun onError(error: Throwable) {
        runOnMainThreadIfNotDisposed { holder ->
            holder.onError.invoke(error)
        }
    }

    private fun runOnMainThreadIfNotDisposed(block: (Holder) -> Unit) {
        val callback: () -> Unit = { getHolder()?.also(block) }

        dispatch_async(dispatch_get_main_queue(), callback.freeze())
    }

    private fun getHolder(): Holder? =
        holderRef.value?.value

    private fun removeHolder(): Holder? =
        holderRef.getAndUpdate { null }?.value

    private fun analyzeEvent(eventId: Long, holder: Holder, socket: Int) {
        val event = holder.controller.state.events.firstOrNull { it.id == eventId } ?: return
        val parsedValue = ValueParser().parseValue(event.value)
        holder.sendData(clientSocket = socket, protoObject = TimeTravelEventValue(eventId = eventId, value = parsedValue))
    }

    private fun Holder.sendData(clientSocket: Int, protoObject: ProtoObject) {
        clients[clientSocket]?.writer?.submit(protoObject)
    }

    private class Holder(
        val controller: TimeTravelController,
        val onError: (Throwable) -> Unit
    ) {
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
