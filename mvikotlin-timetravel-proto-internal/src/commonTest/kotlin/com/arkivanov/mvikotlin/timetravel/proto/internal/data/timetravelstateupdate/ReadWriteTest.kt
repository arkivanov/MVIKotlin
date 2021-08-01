package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
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

    override fun DataWriter.writeObject(obj: TimeTravelStateUpdate) {
        writeTimeTravelStateUpdate(obj)
    }

    override fun DataReader.readObject(): TimeTravelStateUpdate = readTimeTravelStateUpdate()
}
