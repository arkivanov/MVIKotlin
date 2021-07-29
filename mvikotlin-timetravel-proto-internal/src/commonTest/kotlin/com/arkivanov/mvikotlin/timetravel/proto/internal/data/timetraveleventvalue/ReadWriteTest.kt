package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<TimeTravelEventValue>() {

    @Test
    fun writes_and_reads_non_nulls() {
        testWriteRead(
            TimeTravelEventValue(
                eventId = 1L,
                value = ValueNode(
                    name = "name",
                    type = "type",
                    value = "value",
                    children = List(3) { index ->
                        ValueNode(
                            name = "child$index",
                            type = "childType$index",
                            value = "childValue$index"
                        )
                    }
                )
            )
        )
    }

    override fun DataWriter.writeObject(obj: TimeTravelEventValue) {
        writeTimeTravelEventValue(obj)
    }

    override fun DataReader.readObject(): TimeTravelEventValue = readTimeTravelEventValue()
}
