package com.arkivanov.mvikotlin.timetravel

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue.TimeTravelEventValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueParser
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ProtoDecoder
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.ProtoEncoder
import com.arkivanov.mvikotlin.timetravel.server.StateDiff

@ExperimentalTimeTravelApi
class TimeTravelServer(
    private val controller: TimeTravelController = timeTravelController,
) {
    private val messenger = ContentMessenger()
    private var cancellation: Cancellation? = null
    private val protoDecoder = ProtoDecoder()
    private val clients = HashMap<String, Client>()

    fun start() {
        cancellation = messenger.subscribe(::onEvent)
    }

    fun stop() {
        cancellation?.cancel()
        cancellation = null
        clients.values.forEach { it.disposable.dispose() }
        clients.clear()
    }

    private fun onEvent(msg: String) {
        val message = ContentMessage.decode(msg) ?: return

        if (message.receiverId != "server") {
            return
        }

        when (message.type) {
            "connect" -> onConnect(message)
            "proto" -> onProto(message)
            else -> error("Unsupported message type: ${message.type}")
        }
    }

    private fun onConnect(message: ContentMessage) {
        val clientId = message.senderId

        val protoEncoder =
            ProtoEncoder { data, size ->
                messenger.send(
                    ContentMessage(
                        senderId = "server",
                        receiverId = clientId,
                        type = "proto",
                        payload = data.copyOf(size),
                    ).encode()
                )

//                window.postMessage(
//                    message = ContentMessage(
//                        senderId = "server",
//                        receiverId = clientId,
//                        type = "proto",
//                        payload = data.copyOf(size),
//                    ).encode().toJsString(),
//                    targetOrigin = "*",
//                )
            }

        val stateDiff = StateDiff()
        val disposable = controller.states(observer { protoEncoder.encode(stateDiff(it)) })

        clients[clientId] = Client(protoEncoder = protoEncoder, disposable = disposable)
    }

    private fun onProto(message: ContentMessage) {
        val protoObject = protoDecoder.decode(message.requirePayload()) as TimeTravelCommand
        onCommandReceived(protoObject, clientId = message.senderId)
    }

    private fun onCommandReceived(command: TimeTravelCommand, clientId: String) {
        when (command) {
            is TimeTravelCommand.StartRecording -> controller.startRecording()
            is TimeTravelCommand.StopRecording -> controller.stopRecording()
            is TimeTravelCommand.MoveToStart -> controller.moveToStart()
            is TimeTravelCommand.StepBackward -> controller.stepBackward()
            is TimeTravelCommand.StepForward -> controller.stepForward()
            is TimeTravelCommand.MoveToEnd -> controller.moveToEnd()
            is TimeTravelCommand.Cancel -> controller.cancel()
            is TimeTravelCommand.DebugEvent -> controller.debugEvent(eventId = command.eventId)
            is TimeTravelCommand.AnalyzeEvent -> analyzeEvent(eventId = command.eventId, clientId = clientId)
            is TimeTravelCommand.ExportEvents -> error("Unsupported")
            is TimeTravelCommand.ImportEvents -> error("Unsupported")
        }.let {}
    }

    private fun analyzeEvent(eventId: Long, clientId: String) {
        val event = controller.state.events.firstOrNull { it.id == eventId } ?: return
        val parsedValue = ValueParser().parseValue(event.value)
        clients[clientId]?.protoEncoder?.encode(TimeTravelEventValue(eventId = eventId, value = parsedValue))
    }

    private class Client(
        val protoEncoder: ProtoEncoder,
        val disposable: Disposable,
    )
}
