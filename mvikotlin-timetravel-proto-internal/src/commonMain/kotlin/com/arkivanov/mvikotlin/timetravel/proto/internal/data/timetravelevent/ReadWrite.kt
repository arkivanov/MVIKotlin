package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent

import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readLong
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readString
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeLong
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeString

internal fun DataWriter.writeTimeTravelEvent(timeTravelEvent: TimeTravelEvent) {
    writeLong(timeTravelEvent.id)
    writeString(timeTravelEvent.storeName)
    writeEnum(timeTravelEvent.type)
    writeString(timeTravelEvent.valueType)
}

internal fun DataReader.readTimeTravelEvent(): TimeTravelEvent =
    TimeTravelEvent(
        id = readLong(),
        storeName = readString()!!,
        type = readEnum(),
        valueType = readString()!!
    )
