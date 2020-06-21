package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate

data class TimeTravelStateUpdate(
    val eventsUpdate: TimeTravelEventsUpdate,
    val selectedEventIndex: Int,
    val mode: Mode
) : ProtoObject {

    enum class Mode {
        IDLE, RECORDING, STOPPED
    }
}
