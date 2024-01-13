package com.arkivanov.mvikotlin.timetravel

/**
 * Describes the state of the time travel feature
 *
 * @param events a list of the events, see [TimeTravelEvent]
 * @param selectedEventIndex index of the currently selected event
 * @param mode current mode of the time travel feature, see [TimeTravelState.Mode]
 */
data class TimeTravelState(
    val events: List<TimeTravelEvent> = emptyList(),
    val selectedEventIndex: Int = -1,
    val mode: Mode = Mode.IDLE
) {

    enum class Mode {
        IDLE, RECORDING, STOPPED
    }
}
