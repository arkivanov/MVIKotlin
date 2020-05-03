package com.arkivanov.mvikotlin.timetravel.proto.internal

import java.io.Serializable

data class TimeTravelStateUpdate(
    val eventsUpdate: TimeTravelEventsUpdate,
    val selectedEventIndex: Int,
    val mode: Mode
) : Serializable {

    enum class Mode {
        IDLE, RECORDING, STOPPED
    }
}
