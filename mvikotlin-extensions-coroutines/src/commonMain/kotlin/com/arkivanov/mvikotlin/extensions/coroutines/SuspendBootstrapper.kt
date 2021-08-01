package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * An abstract implementation of the [Bootstrapper] that provides interoperability with coroutines.
 * All coroutines are launched in a scope which closes when the [Bootstrapper] is disposed.
 */
@Deprecated(
    message = "Please use CoroutineBootstrapper",
    replaceWith = ReplaceWith("CoroutineBootstrapper<Action>")
)
abstract class SuspendBootstrapper<Action : Any>(
    mainContext: CoroutineContext = Dispatchers.Main
) : Bootstrapper<Action> {

    private val actionConsumer = atomic<(Action) -> Unit>()
    private val scope = CoroutineScope(mainContext)

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

    final override fun invoke() {
        scope.launch {
            bootstrap()
        }
    }

    /**
     * A suspending variant of the [Bootstrapper.invoke] method.
     * The coroutine is launched in a scope which closes when the [Bootstrapper] is disposed.
     */
    abstract suspend fun bootstrap()

    override fun dispose() {
        scope.cancel()
    }
}
