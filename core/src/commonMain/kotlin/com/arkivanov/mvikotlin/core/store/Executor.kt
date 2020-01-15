package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

interface Executor<in Intent, in Action, in State, out Result, out Label> {

    @MainThread
    fun init(stateSupplier: () -> State, resultConsumer: (Result) -> Unit, labelConsumer: (Label) -> Unit)

    @MainThread
    fun handleIntent(intent: Intent)

    @MainThread
    fun handleAction(action: Action)

    @MainThread
    fun dispose()
}
