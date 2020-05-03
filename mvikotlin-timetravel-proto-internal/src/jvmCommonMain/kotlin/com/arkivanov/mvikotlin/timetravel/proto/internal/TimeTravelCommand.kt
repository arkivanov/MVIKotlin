package com.arkivanov.mvikotlin.timetravel.proto.internal

import java.io.Serializable

sealed class TimeTravelCommand : Serializable {

    object StartRecording : TimeTravelCommand()
    object StopRecording : TimeTravelCommand()
    object MoveToStart : TimeTravelCommand()
    object StepBackward : TimeTravelCommand()
    object StepForward : TimeTravelCommand()
    object MoveToEnd : TimeTravelCommand()
    object Cancel : TimeTravelCommand()
    data class DebugEvent(val eventId: Long) : TimeTravelCommand()
}
