package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.readValueNode
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.writeValueNode
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readLong
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeLong

internal fun DataWriter.writeTimeTravelEventValue(timeTravelEventValue: TimeTravelEventValue) {
    writeLong(timeTravelEventValue.eventId)
    writeValueNode(timeTravelEventValue.value)
}

internal fun DataReader.readTimeTravelEventValue(): TimeTravelEventValue =
    TimeTravelEventValue(
        eventId = readLong(),
        value = readValueNode()
    )
