package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.atomic
import com.arkivanov.mvikotlin.core.utils.internal.initialize
import com.arkivanov.mvikotlin.core.utils.internal.requireValue
import com.badoo.reaktive.disposable.scope.DisposableScope

/**
 * Creates Reaktive [Bootstrapper] using the provided [handler].
 *
 * @param handler invoked when the [Bootstrapper] is invoked by the [Store][com.arkivanov.mvikotlin.core.store.Store].
 */
@ExperimentalMviKotlinApi
fun <Action : Any> reaktiveBootstrapper(handler: ReaktiveBootstrapperScope<Action>.() -> Unit): Bootstrapper<Action> =
    object : AbstractBootstrapper<Action>() {
        override fun invoke() {
            handler()
        }
    }

@ExperimentalMviKotlinApi
private abstract class AbstractBootstrapper<Action : Any>(
    private val scope: DisposableScope = DisposableScope(),
) : Bootstrapper<Action>, ReaktiveBootstrapperScope<Action>, DisposableScope by scope {

    private val actionConsumer = atomic<(Action) -> Unit>()

    override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    override fun dispatch(action: Action) {
        actionConsumer.requireValue().invoke(action)
    }

    override fun dispose() {
        scope.dispose()
    }
}
