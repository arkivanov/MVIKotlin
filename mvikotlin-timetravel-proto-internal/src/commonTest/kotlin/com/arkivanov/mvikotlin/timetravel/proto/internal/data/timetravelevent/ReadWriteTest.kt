package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<TimeTravelEvent>() {

    @Test
    fun writes_and_reads_TimeTravelEvent() {
        testWriteRead(
            TimeTravelEvent(
                id = 1L,
                storeName = "store",
                type = StoreEventType.INTENT,
                valueType = "value_type"
            )
        )
    }

    override fun DataWriter.writeObject(obj: TimeTravelEvent) {
        writeTimeTravelEvent(obj)
    }

    override fun DataReader.readObject(): TimeTravelEvent = readTimeTravelEvent()
}
