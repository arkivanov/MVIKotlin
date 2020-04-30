package com.arkivanov.mvikotlin.timetravel.proto

import java.io.Serializable

sealed class TimeTravelEventsUpdate : Serializable {

    data class All(val events: List<TimeTravelEvent>) : TimeTravelEventsUpdate()
    data class New(val events: List<TimeTravelEvent>) : TimeTravelEventsUpdate()
}

