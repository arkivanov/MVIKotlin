package com.arkivanov.mvikotlin.timetravel.client.internal.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Event

internal val eventToIntent: Event.() -> Intent =
    {
        when (this) {
            is Event.ConnectClicked -> Intent.Connect
            is Event.DisconnectClicked -> Intent.Disconnect
            is Event.StartRecordingClicked -> Intent.StartRecording
            is Event.StopRecordingClicked -> Intent.StopRecording
            is Event.MoveToStartClicked -> Intent.MoveToStart
            is Event.StepBackwardClicked -> Intent.StepBackward
            is Event.StepForwardClicked -> Intent.StepForward
            is Event.MoveToEndClicked -> Intent.MoveToEnd
            is Event.CancelClicked -> Intent.Cancel
            is Event.DebugEventClicked -> Intent.DebugEvent
            is Event.EventSelected -> Intent.SelectEvent(index = index)
        }
    }
