package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import kotlinx.serialization.SerializationStrategy

internal interface TimeTravelStore<in Intent : Any, State : Any, out Label : Any> : Store<Intent, State, Label> {

    fun events(observer: Observer<Event<*, State>>): Disposable

    @MainThread
    fun restoreState()

    @MainThread
    fun process(type: StoreEventType, value: Any)

    @MainThread
    fun debug(type: StoreEventType, value: Any, state: Any)

    data class Event<T : Any, State : Any>(
        val type: StoreEventType,
        val value: T,
        val valueSerializer: SerializationStrategy<T>,
        val state: State,
        val stateSerializer: SerializationStrategy<State>,
    )
}
