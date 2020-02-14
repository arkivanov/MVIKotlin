package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue

abstract class AbstractBootstrapper<Action> : Bootstrapper<Action> {

    private val actionConsumer = lateinitAtomicReference<(Action) -> Unit>()

    final override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    protected fun dispatch(action: Action) {
        actionConsumer.requireValue.invoke(action)
    }

    override fun dispose() {
    }
}
