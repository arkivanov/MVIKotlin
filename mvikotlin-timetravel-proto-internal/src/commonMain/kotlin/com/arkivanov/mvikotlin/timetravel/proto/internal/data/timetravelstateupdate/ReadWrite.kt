package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.readTimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.writeTimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataReader
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.DataWriter
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.readInt
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeEnum
import com.arkivanov.mvikotlin.timetravel.proto.internal.io.writeInt

internal fun DataWriter.writeTimeTravelStateUpdate(timeTravelStateUpdate: TimeTravelStateUpdate) {
    writeTimeTravelEventsUpdate(timeTravelStateUpdate.eventsUpdate)
    writeInt(timeTravelStateUpdate.selectedEventIndex)
    writeEnum(timeTravelStateUpdate.mode)
}

internal fun DataReader.readTimeTravelStateUpdate(): TimeTravelStateUpdate =
    TimeTravelStateUpdate(
        eventsUpdate = readTimeTravelEventsUpdate(),
        selectedEventIndex = readInt(),
        mode = readEnum()
    )
