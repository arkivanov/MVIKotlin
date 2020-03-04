package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue

class SimpleBootstrapper<Action>(
    private vararg val actions: Action
) : Bootstrapper<Action> {

    private val actionConsumer = lateinitAtomicReference<(Action) -> Unit>()

    override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    override fun dispose() {
        // no-op
    }

    override fun invoke() {
        actions.forEach(actionConsumer.requireValue::invoke)
    }
}
