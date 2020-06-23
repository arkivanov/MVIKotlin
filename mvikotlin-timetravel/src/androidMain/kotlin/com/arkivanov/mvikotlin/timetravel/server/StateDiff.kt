package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate

internal class StateDiff {

    private var previousState: TimeTravelState? = null

    operator fun invoke(state: TimeTravelState): TimeTravelStateUpdate {
        val previousState = previousState

        val update =
            TimeTravelStateUpdate(
                eventsUpdate = diffEvents(new = state.events, previous = previousState?.events),
                selectedEventIndex = state.selectedEventIndex,
                mode = state.mode.toProto()
            )

        this.previousState = state

        return update
    }

    private fun diffEvents(new: List<TimeTravelEvent>, previous: List<TimeTravelEvent>?): TimeTravelEventsUpdate =
        when {
            previous == null -> TimeTravelEventsUpdate.All(new.toProto())
            new.size > previous.size -> TimeTravelEventsUpdate.New(new.subList(previous.size, new.size).toProto())
            new.size == previous.size -> TimeTravelEventsUpdate.New(emptyList())
            else -> TimeTravelEventsUpdate.All(new.toProto())
        }
}
