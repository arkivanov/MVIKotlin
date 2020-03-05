package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent

internal interface TimeTravelStore<in Intent : Any, out State : Any, out Label : Any> : Store<Intent, State, Label> {

    val name: String
    val eventProcessor: EventProcessor
    val eventDebugger: EventDebugger

    @MainThread
    fun events(observer: Observer<TimeTravelEvent>): Disposable

    @MainThread
    fun init()

    @MainThread
    fun restoreState()

    interface EventProcessor {
        @MainThread
        fun process(type: StoreEventType, value: Any)
    }

    interface EventDebugger {
        @MainThread
        fun debug(event: TimeTravelEvent)
    }
}
