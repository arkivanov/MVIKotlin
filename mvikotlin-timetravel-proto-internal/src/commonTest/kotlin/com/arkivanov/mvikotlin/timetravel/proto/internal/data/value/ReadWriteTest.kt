package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.AbstractReadWriteTest
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import kotlin.test.Test

internal class ReadWriteTest : AbstractReadWriteTest<ValueNode>() {

    @Test
    fun writes_and_reads_non_nulls() {
        testWriteRead(
            ValueNode(
                name = "name",
                type = "type",
                value = "value",
                children = List(3) { index ->
                    ValueNode(
                        name = "child$index",
                        type = "childType$index",
                        value = "childValue$index",
                        children = List(3) { subIndex ->
                            ValueNode(
                                name = "subChild$subIndex",
                                type = "subChildType$subIndex",
                                value = "subChildValue$subIndex"
                            )
                        }
                    )
                }
            )
        )
    }

    @Test
    fun writes_and_reads_nulls() {
        testWriteRead(
            ValueNode(
                name = null,
                type = "type",
                value = null,
                children = List(3) { index ->
                    ValueNode(
                        name = null,
                        type = "childType$index",
                        value = null
                    )
                }
            )
        )
    }

    override fun DataWriter.writeObject(obj: ValueNode) {
        writeValueNode(obj)
    }

    override fun DataReader.readObject(): ValueNode = readValueNode()
}
