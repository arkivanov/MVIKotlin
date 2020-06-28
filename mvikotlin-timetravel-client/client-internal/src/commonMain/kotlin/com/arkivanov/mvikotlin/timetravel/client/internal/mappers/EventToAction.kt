package com.arkivanov.mvikotlin.timetravel.client.internal.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Action
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Event

internal val eventToAction: Event.() -> Action? =
    {
        when (this) {
            is Event.ImportEventsClicked -> Action.ImportEvents

            is Event.ConnectClicked,
            is Event.DisconnectClicked,
            is Event.StartRecordingClicked,
            is Event.StopRecordingClicked,
            is Event.MoveToStartClicked,
            is Event.StepBackwardClicked,
            is Event.StepForwardClicked,
            is Event.MoveToEndClicked,
            is Event.CancelClicked,
            is Event.DebugEventClicked,
            is Event.EventSelected,
            is Event.ExportEventsClicked,
            is Event.ImportEventsConfirmed -> null
        }
    }
