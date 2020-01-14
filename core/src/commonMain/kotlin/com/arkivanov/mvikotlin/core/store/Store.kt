package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer

interface Store<in Intent, out State, out Label> : Disposable {

    val state: State

    fun states(observer: Observer<State>): Disposable

    fun labels(observer: Observer<Label>): Disposable

    fun accept(intent: Intent)
}
