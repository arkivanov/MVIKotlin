package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.atomic
import com.arkivanov.mvikotlin.core.utils.internal.initialize
import com.arkivanov.mvikotlin.core.utils.internal.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Creates coroutine [Bootstrapper] using the provided [handler].
 *
 * @param mainContext main [CoroutineContext] to be used by default when launching coroutines, default value is [Dispatchers.Main].
 * @param handler invoked when the [Bootstrapper] is invoked by the [Store][com.arkivanov.mvikotlin.core.store.Store].
 */
@ExperimentalMviKotlinApi
fun <Action : Any> coroutineBootstrapper(
    mainContext: CoroutineContext = Dispatchers.Main,
    handler: CoroutineBootstrapperScope<Action>.() -> Unit,
): Bootstrapper<Action> =
    object : AbstractBootstrapper<Action>(scope = CoroutineScope(mainContext)) {
        override fun invoke() {
            handler()
        }
    }

@ExperimentalMviKotlinApi
private abstract class AbstractBootstrapper<Action : Any>(
    private val scope: CoroutineScope,
) : Bootstrapper<Action>, CoroutineBootstrapperScope<Action>, CoroutineScope by scope {

    private val actionConsumer = atomic<(Action) -> Unit>()

    override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    override fun dispatch(action: Action) {
        actionConsumer.requireValue().invoke(action)
    }

    override fun dispose() {
        scope.cancel()
    }
}
