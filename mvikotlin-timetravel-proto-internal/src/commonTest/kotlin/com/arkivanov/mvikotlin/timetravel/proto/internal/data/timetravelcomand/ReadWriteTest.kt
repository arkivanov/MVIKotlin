package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<TimeTravelCommand>() {

    @Test
    fun writes_and_reads_TimeTravelCommand_StartRecording() {
        testWriteRead(TimeTravelCommand.StartRecording)
    }

    @Test
    fun writes_and_reads_TimeTravelCommand_StopRecording() {
        testWriteRead(TimeTravelCommand.StopRecording)
    }

    @Test
    fun writes_and_reads_TimeTravelCommand_MoveToStart() {
        testWriteRead(TimeTravelCommand.MoveToStart)
    }

    @Test
    fun writes_and_reads_TimeTravelCommand_StepBackward() {
        testWriteRead(TimeTravelCommand.StepBackward)
    }

    @Test
    fun writes_and_reads_TimeTravelCommand_StepForward() {
        testWriteRead(TimeTravelCommand.StepForward)
    }

    @Test
    fun writes_and_reads_TimeTravelCommand_MoveToEnd() {
        testWriteRead(TimeTravelCommand.MoveToEnd)
    }

    @Test
    fun writes_and_reads_TimeTravelCommand_Cancel() {
        testWriteRead(TimeTravelCommand.Cancel)
    }

    @Test
    fun writes_and_reads_TimeTravelCommand_DebugEvent() {
        testWriteRead(TimeTravelCommand.DebugEvent(eventId = 123L))
    }

    override fun DataWriter.writeObject(obj: TimeTravelCommand) {
        writeTimeTravelCommand(obj)
    }

    override fun DataReader.readObject(): TimeTravelCommand = readTimeTravelCommand()
}
