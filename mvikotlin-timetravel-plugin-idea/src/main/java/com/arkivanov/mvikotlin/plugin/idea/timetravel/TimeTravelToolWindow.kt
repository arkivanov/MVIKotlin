package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.plugin.idea.timetravel.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.TimeTravelStateUpdate
import java.io.IOException
import javax.swing.JComponent

class TimeTravelToolWindow {

    private val view = TimeTravelView(ViewListener())
    private val client = TimeTravelClient(host = "localhost", port = DEFAULT_PORT, callbacks = TimeTravelClientCallbacks())
    private val adbPathProvider = AdbPathProvider()
    private var viewState = TimeTravelView.State()

    val content: JComponent = view.content

    private fun updateViewState(eventsUpdate: TimeTravelEventsUpdate? = null, update: TimeTravelView.State.() -> TimeTravelView.State) {
        viewState = update(viewState)
        view.render(viewState, eventsUpdate)
    }

    private fun forwardPort(): Boolean {
        try {
            val adbPath = adbPathProvider.get() ?: return false
            val params = listOf(adbPath, "forward", "tcp:$DEFAULT_PORT", "tcp:$DEFAULT_PORT")
            val process = exec(params)
            if (process.waitFor() == 0) {
                log("Port forwarded successfully")
                return true
            } else {
                log("Failed to forward the port: ${process.readError()}")
            }
        } catch (e: Exception) {
            log("Failed to forward the port: ${e.message}")
            e.printStackTrace()
        }

        return false
    }

    private inner class ViewListener : TimeTravelView.Listener {
        override fun onConnect() {
            if (!client.isDisconnected) {
                return
            }

            if (!forwardPort()) {
                showError("Error forwarding port via ADB. Make sure there is only one device connected.")

                return
            }

            client.connect()
        }

        override fun onDisconnect() {
            client.disconnect()
        }

        override fun onStartRecording() {
            client.send(TimeTravelCommand.StartRecording)
        }

        override fun onStopRecording() {
            client.send(TimeTravelCommand.StopRecording)
        }

        override fun onMoveToStart() {
            client.send(TimeTravelCommand.MoveToStart)
        }

        override fun onStepBackward() {
            client.send(TimeTravelCommand.StepBackward)
        }

        override fun onStepForward() {
            client.send(TimeTravelCommand.StepForward)
        }

        override fun onMoveToEnd() {
            client.send(TimeTravelCommand.MoveToEnd)
        }

        override fun onCancel() {
            client.send(TimeTravelCommand.Cancel)
        }

        override fun onDebug(eventId: Long) {
            client.send(TimeTravelCommand.DebugEvent(eventId = eventId))
        }
    }

    private inner class TimeTravelClientCallbacks : TimeTravelClient.Callbacks {
        override fun onConnecting() {
            updateViewState { copy(connectionStatus = ConnectionStatus.CONNECTING) }
        }

        override fun onConnected() {
            updateViewState { copy(connectionStatus = ConnectionStatus.CONNECTED) }
        }

        override fun onDisconnected() {
            updateViewState(TimeTravelEventsUpdate.All(emptyList())) { TimeTravelView.State() }
        }

        override fun onError(error: IOException) {
            showError(error.toString())
        }

        override fun onStateUpdateReceived(stateUpdate: TimeTravelStateUpdate) {
            updateViewState(stateUpdate.eventsUpdate) {
                copy(selectedEventIndex = stateUpdate.selectedEventIndex, mode = stateUpdate.mode)
            }
        }
    }
}
