package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * An abstract implementation of the [Bootstrapper] that exposes a [CoroutineScope] for coroutines launching.
 *
 * @param mainContext a [CoroutineContext] to be used by the exposed [CoroutineScope]
 */
abstract class CoroutineBootstrapper<Action : Any>(
    mainContext: CoroutineContext = Dispatchers.Main
) : Bootstrapper<Action> {

    private val actionConsumer = atomic<(Action) -> Unit>()

    /**
     * A [CoroutineScope] that can be used by the [CoroutineBootstrapper] descendants to launch coroutines.
     * The [CoroutineScope] is automatically cancelled on dispose.
     */
    protected val scope: CoroutineScope = CoroutineScope(mainContext)

    final override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    /**
     * Dispatches the `Action` to the [Store]
     *
     * @param action an `Action` to be dispatched
     */
    protected fun dispatch(action: Action) {
        actionConsumer.requireValue().invoke(action)
    }

    override fun dispose() {
        scope.cancel()
    }
}
