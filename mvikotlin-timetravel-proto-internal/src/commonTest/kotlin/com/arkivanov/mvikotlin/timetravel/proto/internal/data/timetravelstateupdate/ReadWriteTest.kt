package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ParsedValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<TimeTravelStateUpdate>() {

    @Test
    fun writes_and_reads_TimeTravelStateUpdate() {
        testWriteRead(
            TimeTravelStateUpdate(
                eventsUpdate = TimeTravelEventsUpdate.All(
                    events = listOf(
                        TimeTravelEvent(
                            id = 1L,
                            storeName = "store",
                            type = StoreEventType.INTENT,
                            value = ParsedValue.Object.String(value = "string")
                        ),
                        TimeTravelEvent(
                            id = 2L,
                            storeName = "store",
                            type = StoreEventType.ACTION,
                            value = ParsedValue.Object.Unparsed(type = "unparsedType", value = "unparsedValue")
                        )
                    )
                ),
                selectedEventIndex = 234,
                mode = TimeTravelStateUpdate.Mode.STOPPED
            )
        )
    }

    override fun DataWriter.writeObject(obj: TimeTravelStateUpdate) {
        writeTimeTravelStateUpdate(obj)
    }

    override fun DataReader.readObject(): TimeTravelStateUpdate = readTimeTravelStateUpdate()
}
