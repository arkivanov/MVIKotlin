package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.export.TimeTravelExportSerializer
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue.TimeTravelEventValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelexport.TimeTravelExport
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueParser
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ReaderThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.WriterThread
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.closeSafe
import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread

internal class TimeTravelServerImpl(
    private val runOnMainThread: (() -> Unit) -> Unit,
    private val controller: TimeTravelController,
    private val port: Int,
    private val exportSerializer: TimeTravelExportSerializer,
    private val onError: (Throwable) -> Unit
) {

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
                ReaderThread<TimeTravelCommand>(
                    socket = socket,
                    onRead = { onCommandReceived(command = it, sender = socket) },
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

    private fun onCommandReceived(command: TimeTravelCommand, sender: Socket) {
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
                is TimeTravelCommand.AnalyzeEvent -> analyzeEvent(eventId = command.eventId, sender = sender)
                is TimeTravelCommand.ExportEvents -> exportEvents(sender)
                is TimeTravelCommand.ImportEvents -> importEvents(command.data)
            }.let {}
        }
    }

    private fun analyzeEvent(eventId: Long, sender: Socket) {
        val event = controller.state.events.firstOrNull { it.id == eventId } ?: return
        val parsedValue = ValueParser().parseValue(event.value)
        sendData(sender, TimeTravelEventValue(eventId = eventId, value = parsedValue))
    }

    private fun exportEvents(sender: Socket) {
        val export = controller.export()

        thread {
            val result = exportSerializer.serialize(export)
            runOnMainThreadIfNotDisposed {
                when (result) {
                    is TimeTravelExportSerializer.Result.Success -> sendData(sender, TimeTravelExport(result.data))
                    is TimeTravelExportSerializer.Result.Error -> onError(result.exception)
                }.let {}
            }
        }
    }

    private fun importEvents(data: ByteArray) {
        thread {
            val result = exportSerializer.deserialize(data)
            runOnMainThreadIfNotDisposed {
                when (result) {
                    is TimeTravelExportSerializer.Result.Success -> controller.import(result.data)
                    is TimeTravelExportSerializer.Result.Error -> onError(result.exception)
                }.let {}
            }
        }
    }

    private fun onError(error: IOException) {
        runOnMainThreadIfNotDisposed {
            onError.invoke(error)
        }
    }

    private inline fun runOnMainThreadIfNotDisposed(crossinline block: () -> Unit) {
        runOnMainThread {
            if (connectionThread != null) {
                block()
            }
        }
    }

    private fun sendData(clientSocket: Socket, protoObject: ProtoObject) {
        clients[clientSocket]?.writer?.write(protoObject)
    }

    private class Client(
        val socket: Socket,
        val reader: ReaderThread<*>,
        val writer: WriterThread,
        val disposable: Disposable
    )
}
