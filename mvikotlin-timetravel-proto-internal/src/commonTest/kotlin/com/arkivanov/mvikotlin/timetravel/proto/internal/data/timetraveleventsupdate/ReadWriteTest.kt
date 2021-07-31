package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<TimeTravelEventsUpdate>() {


    @Test
    fun writes_and_reads_TimeTravelEventsUpdate_All() {
        testWriteRead(
            TimeTravelEventsUpdate.All(
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
            )
        )
    }

    @Test
    fun writes_and_reads_TimeTravelEventsUpdate_New() {
        testWriteRead(
            TimeTravelEventsUpdate.New(
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
            )
        )
    }

    override fun DataWriter.writeObject(obj: TimeTravelEventsUpdate) {
        writeTimeTravelEventsUpdate(obj)
    }

    override fun DataReader.readObject(): TimeTravelEventsUpdate = readTimeTravelEventsUpdate()
}
