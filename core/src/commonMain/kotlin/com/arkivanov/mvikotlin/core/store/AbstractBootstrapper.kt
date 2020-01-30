package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue

abstract class AbstractBootstrapper<Action> : Bootstrapper<Action> {

    private val actionConsumer = lazyAtomicReference<(Action) -> Unit>()

    final override fun init(actionConsumer: (Action) -> Unit) {
        check(this.actionConsumer.value == null) { "Bootstrapper is already initialized" }

        this.actionConsumer.value = actionConsumer
    }

    protected fun dispatch(action: Action) {
        actionConsumer.requireValue.invoke(action)
    }

    override fun dispose() {
    }
}
