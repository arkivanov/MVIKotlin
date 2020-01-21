package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Store

internal interface TimeTravelStore<in Intent, out State, out Label> : Store<Intent, State, Label> {

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
        fun process(type: StoreEventType, value: Any?)
    }

    interface EventDebugger {
        @MainThread
        fun debug(event: TimeTravelEvent)
    }
}
