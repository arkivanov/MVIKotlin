package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue

/**
 * Simple implementation of the [Bootstrapper].
 * Accepts an array of the `Actions` and dispatches them one by one.
 *
 * @param actions an array of the `Actions` to be dispatched
 */
class SimpleBootstrapper<out Action : Any>(
    private vararg val actions: Action
) : Bootstrapper<Action> {

    private val actionConsumer = atomic<(Action) -> Unit>()

    override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    override fun dispose() {
        // no-op
    }

    override fun invoke() {
        actions.forEach(actionConsumer.requireValue()::invoke)
    }
}
