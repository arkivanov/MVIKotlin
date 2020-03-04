package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class SuspendBootstrapper<Action>(
    mainContext: CoroutineContext = Dispatchers.Main
) : Bootstrapper<Action> {

    private val actionConsumer = lateinitAtomicReference<(Action) -> Unit>()
    private val scope = CoroutineScope(mainContext)

    final override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    protected fun dispatch(action: Action) {
        actionConsumer.requireValue.invoke(action)
    }

    final override fun invoke() {
        scope.launch {
            bootstrap()
        }
    }

    abstract suspend fun bootstrap()

    override fun dispose() {
        scope.cancel()
    }
}
