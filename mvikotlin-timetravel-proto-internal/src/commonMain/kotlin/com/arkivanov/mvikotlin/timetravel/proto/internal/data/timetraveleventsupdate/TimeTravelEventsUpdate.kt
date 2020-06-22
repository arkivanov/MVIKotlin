package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent

sealed class TimeTravelEventsUpdate {

    data class All(val events: List<TimeTravelEvent>) : TimeTravelEventsUpdate()
    data class New(val events: List<TimeTravelEvent>) : TimeTravelEventsUpdate()
}
