package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.readTimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.writeTimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readList
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeCollection
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeEnum

//region Write

internal fun DataWriter.writeTimeTravelEventsUpdate(timeTravelEventsUpdate: TimeTravelEventsUpdate) {
    when (timeTravelEventsUpdate) {
        is TimeTravelEventsUpdate.All -> writeTimeTravelEventsUpdateAll(timeTravelEventsUpdate)
        is TimeTravelEventsUpdate.New -> writeTimeTravelEventsUpdateNew(timeTravelEventsUpdate)
    }.let {}
}

private fun DataWriter.writeTimeTravelEventsUpdateAll(timeTravelEventsUpdate: TimeTravelEventsUpdate.All) {
    writeEnum(Type.ALL)
    writeCollection(timeTravelEventsUpdate.events) {
        writeTimeTravelEvent(it)
    }
}

private fun DataWriter.writeTimeTravelEventsUpdateNew(timeTravelEventsUpdate: TimeTravelEventsUpdate.New) {
    writeEnum(Type.NEW)
    writeCollection(timeTravelEventsUpdate.events) {
        writeTimeTravelEvent(it)
    }
}

//endregion

//region Read

internal fun DataReader.readTimeTravelEventsUpdate(): TimeTravelEventsUpdate =
    when (readEnum<Type>()) {
        Type.ALL -> readTimeTravelEventsUpdateAll()
        Type.NEW -> readTimeTravelEventsUpdateNew()
    }

private fun DataReader.readTimeTravelEventsUpdateAll(): TimeTravelEventsUpdate.All =
    TimeTravelEventsUpdate.All(
        events = readList { readTimeTravelEvent() }!!
    )

private fun DataReader.readTimeTravelEventsUpdateNew(): TimeTravelEventsUpdate.New =
    TimeTravelEventsUpdate.New(
        events = readList { readTimeTravelEvent() }!!
    )

//endregion

private enum class Type {

    ALL, NEW
}
