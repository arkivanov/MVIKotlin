package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer

interface Store<in Intent, out State, out Label> {

    val state: State
    val isDisposed: Boolean

    @MainThread
    fun states(observer: Observer<State>): Disposable

    @MainThread
    fun labels(observer: Observer<Label>): Disposable

    @MainThread
    fun accept(intent: Intent)

    @MainThread
    fun dispose()
}
