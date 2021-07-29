package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import kotlin.test.Test
import kotlin.test.assertEquals

class EncodeDecodeTest {

    @Test
    fun encodes_and_decodes_TimeTravelStateUpdate_All() {
        testEncodeDecode(
            TimeTravelStateUpdate(
                eventsUpdate = TimeTravelEventsUpdate.All(
                    events = listOf(
                        TimeTravelEvent(
                            id = 1L,
                            storeName = "store",
                            type = StoreEventType.INTENT,
                            valueType = "value_type_1"
                        ),
                        TimeTravelEvent(
                            id = 2L,
                            storeName = "store",
                            type = StoreEventType.ACTION,
                            valueType = "value_type_2"
                        )
                    )
                ),
                selectedEventIndex = 234,
                mode = TimeTravelStateUpdate.Mode.STOPPED
            )
        )
    }

    @Test
    fun encodes_and_decodes_TimeTravelStateUpdate_New() {
        testEncodeDecode(
            TimeTravelStateUpdate(
                eventsUpdate = TimeTravelEventsUpdate.New(
                    events = listOf(
                        TimeTravelEvent(
                            id = 1L,
                            storeName = "store",
                            type = StoreEventType.INTENT,
                            valueType = "value_type_1"
                        ),
                        TimeTravelEvent(
                            id = 2L,
                            storeName = "store",
                            type = StoreEventType.ACTION,
                            valueType = "value_type_2"
                        )
                    )
                ),
                selectedEventIndex = 234,
                mode = TimeTravelStateUpdate.Mode.STOPPED
            )
        )
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_StartRecording() {
        testEncodeDecode(TimeTravelCommand.StartRecording)
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_StopRecording() {
        testEncodeDecode(TimeTravelCommand.StopRecording)
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_MoveToStart() {
        testEncodeDecode(TimeTravelCommand.MoveToStart)
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_StepBackward() {
        testEncodeDecode(TimeTravelCommand.StepBackward)
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_StepForward() {
        testEncodeDecode(TimeTravelCommand.StepForward)
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_MoveToEnd() {
        testEncodeDecode(TimeTravelCommand.MoveToEnd)
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_Cancel() {
        testEncodeDecode(TimeTravelCommand.Cancel)
    }

    @Test
    fun encodes_and_decodes_TimeTravelCommand_DebugEvent() {
        testEncodeDecode(TimeTravelCommand.DebugEvent(eventId = 123L))
    }

    private fun testEncodeDecode(obj: ProtoObject) {
        var decodedObj: ProtoObject? = null
        val decoder = ProtoDecoder()
        val frameDecoder = ProtoFrameDecoder { data -> decodedObj = decoder.decode(data) }
        val frameEncoder = ProtoFrameEncoder(frameDecoder::accept)
        val encoder = ProtoEncoder(frameEncoder::accept)
        encoder.encode(obj)

        assertEquals(obj, decodedObj)
    }
}
