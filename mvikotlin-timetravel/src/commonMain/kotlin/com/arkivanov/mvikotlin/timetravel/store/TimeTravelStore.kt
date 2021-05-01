package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer

internal interface TimeTravelStore<in Intent : Any, out State : Any, out Label : Any> : Store<Intent, State, Label> {

    fun events(observer: Observer<Event>): Disposable

    @MainThread
    fun restoreState()

    @MainThread
    fun process(type: StoreEventType, value: Any)

    @MainThread
    fun debug(type: StoreEventType, value: Any, state: Any)

    data class Event(
        val type: StoreEventType,
        val value: Any,
        val state: Any
    )
}
