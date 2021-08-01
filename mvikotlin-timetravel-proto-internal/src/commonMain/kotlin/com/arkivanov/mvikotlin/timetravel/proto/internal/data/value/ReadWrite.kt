package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readList
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readString
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeCollection
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeString

internal fun DataWriter.writeValueNode(valueNode: ValueNode) {
    writeString(valueNode.name)
    writeString(valueNode.type)
    writeString(valueNode.value)
    writeCollection(valueNode.children, DataWriter::writeValueNode)
}

internal fun DataReader.readValueNode(): ValueNode =
    ValueNode(
        name = readString(),
        type = readString()!!,
        value = readString(),
        children = readList { readValueNode() }!!
    )
