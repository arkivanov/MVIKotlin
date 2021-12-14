package com.arkivanov.mvikotlin.timetravel.client.internal.client

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.observable.Observable

interface Connector {

    @EventsOnMainScheduler
    fun connect(): Observable<Event>

    sealed class Event {
        class Connected(val writer: (TimeTravelCommand) -> Unit) : Event()
        class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Event()
        class EventValue(val eventId: Long, val value: ValueNode) : Event()
        class ExportEvents(val data: ByteArray) : Event()
        class Error(val text: String?) : Event()
    }
}
