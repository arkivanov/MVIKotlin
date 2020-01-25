package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue

abstract class BaseBootstrapper<Action> : Bootstrapper<Action> {

    private val actionConsumer = lazyAtomicReference<(Action) -> Unit>()

    final override fun bootstrap(actionConsumer: (Action) -> Unit) {
        check(this.actionConsumer.value == null) { "Bootstrapper is already executed" }

        this.actionConsumer.value = actionConsumer
        bootstrap()
    }

    abstract fun bootstrap()

    protected fun dispatch(action: Action) {
        actionConsumer.requireValue.invoke(action)
    }

    override fun dispose() {
    }
}
