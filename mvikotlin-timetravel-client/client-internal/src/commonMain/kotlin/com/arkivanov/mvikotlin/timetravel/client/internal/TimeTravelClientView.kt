package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Model
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.Value

interface TimeTravelClientView : MviView<Model, Event> {

    fun showError(text: String)

    data class Model(
        val events: List<String>,
        val currentEventIndex: Int,
        val buttons: Buttons,
        val selectedEventIndex: Int,
        val selectedEventValue: Value?
    ) {
        data class Buttons(
            val isConnectEnabled: Boolean,
            val isDisconnectEnabled: Boolean,
            val isStartRecordingEnabled: Boolean,
            val isStopRecordingEnabled: Boolean,
            val isMoveToStartEnabled: Boolean,
            val isStepBackwardEnabled: Boolean,
            val isStepForwardEnabled: Boolean,
            val isMoveToEndEnabled: Boolean,
            val isCancelEnabled: Boolean,
            val isDebugEventEnabled: Boolean
        )
    }

    sealed class Event {
        object ConnectClicked : Event()
        object DisconnectClicked : Event()
        object StartRecordingClicked : Event()
        object StopRecordingClicked : Event()
        object MoveToStartClicked : Event()
        object StepBackwardClicked : Event()
        object StepForwardClicked : Event()
        object MoveToEndClicked : Event()
        object CancelClicked : Event()
        object DebugEventClicked : Event()
        data class EventSelected(val index: Int) : Event()
    }
}
