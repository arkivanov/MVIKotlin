package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject

sealed class TimeTravelCommand : ProtoObject {

    object StartRecording : TimeTravelCommand()
    object StopRecording : TimeTravelCommand()
    object MoveToStart : TimeTravelCommand()
    object StepBackward : TimeTravelCommand()
    object StepForward : TimeTravelCommand()
    object MoveToEnd : TimeTravelCommand()
    object Cancel : TimeTravelCommand()
    data class DebugEvent(val eventId: Long) : TimeTravelCommand()
    data class AnalyzeEvent(val eventId: Long) : TimeTravelCommand()
    object ExportEvents : TimeTravelCommand()
    class ImportEvents(val data: ByteArray) : TimeTravelCommand()
}
