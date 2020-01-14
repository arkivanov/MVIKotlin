package com.arkivanov.mvikotlin.core.store

interface Executor<in Intent, in Action, in State, out Result, out Label> {

    fun init(stateSupplier: () -> State, resultConsumer: (Result) -> Unit, labelConsumer: (Label) -> Unit)

    fun handleIntent(intent: Intent)

    fun handleAction(action: Action)

    fun dispose()
}
