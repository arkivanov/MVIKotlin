package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand

import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readLong
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeLong

internal fun DataWriter.writeTimeTravelCommand(timeTravelCommand: TimeTravelCommand) {
    when (timeTravelCommand) {
        is TimeTravelCommand.StartRecording -> writeEnum(Type.START_RECORDING)
        is TimeTravelCommand.StopRecording -> writeEnum(Type.STOP_RECORDING)
        is TimeTravelCommand.MoveToStart -> writeEnum(Type.MOVE_TO_START)
        is TimeTravelCommand.StepBackward -> writeEnum(Type.STEP_BACKWARD)
        is TimeTravelCommand.StepForward -> writeEnum(Type.STEP_FORWARD)
        is TimeTravelCommand.MoveToEnd -> writeEnum(Type.MOVE_TO_END)
        is TimeTravelCommand.Cancel -> writeEnum(Type.CANCEL)

        is TimeTravelCommand.DebugEvent -> {
            writeEnum(Type.DEBUG_EVENT)
            writeLong(timeTravelCommand.eventId)
        }
    }.let {}
}

internal fun DataReader.readTimeTravelCommand(): TimeTravelCommand =
    when (readEnum<Type>()) {
        Type.START_RECORDING -> TimeTravelCommand.StartRecording
        Type.STOP_RECORDING -> TimeTravelCommand.StopRecording
        Type.MOVE_TO_START -> TimeTravelCommand.MoveToStart
        Type.STEP_BACKWARD -> TimeTravelCommand.StepBackward
        Type.STEP_FORWARD -> TimeTravelCommand.StepForward
        Type.MOVE_TO_END -> TimeTravelCommand.MoveToEnd
        Type.CANCEL -> TimeTravelCommand.Cancel
        Type.DEBUG_EVENT -> TimeTravelCommand.DebugEvent(eventId = readLong())
    }

private enum class Type {
    START_RECORDING,
    STOP_RECORDING,
    MOVE_TO_START,
    STEP_BACKWARD,
    STEP_FORWARD,
    MOVE_TO_END,
    CANCEL,
    DEBUG_EVENT
}
