package com.arkivanov.mvikotlin.core.debug.store.timetravel

data class TimeTravelState(
    val events: List<TimeTravelEvent> = emptyList(),
    val selectedEventIndex: Int = -1,
    val mode: Mode = Mode.IDLE
) {

    enum class Mode {
        IDLE, RECORDING, STOPPED
    }
}
