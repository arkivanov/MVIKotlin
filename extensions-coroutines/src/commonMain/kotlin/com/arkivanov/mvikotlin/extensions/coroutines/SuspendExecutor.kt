package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Executor.Callbacks
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class SuspendExecutor<in Intent, in Action, in State, Result, Label>(
    mainContext: CoroutineContext = Dispatchers.Main
) : Executor<Intent, Action, State, Result, Label> {

    private val callbacks = lazyAtomicReference<Callbacks<State, Result, Label>>()
    private val getState: () -> State = { callbacks.requireValue.state }
    private val scope = CoroutineScope(mainContext)

    override fun init(callbacks: Callbacks<State, Result, Label>) {
        check(this.callbacks.value == null) { "Executor is already initialized" }

        this.callbacks.value = callbacks
    }

    final override fun handleIntent(intent: Intent) {
        scope.launch {
            executeIntent(intent, getState)
        }
    }

    @MainThread
    protected open suspend fun executeIntent(intent: Intent, getState: () -> State) {
    }

    final override fun handleAction(action: Action) {
        scope.launch {
            executeAction(action, getState)
        }
    }

    @MainThread
    protected open suspend fun executeAction(action: Action, getState: () -> State) {
    }

    override fun dispose() {
        scope.cancel()
    }

    @MainThread
    protected fun dispatch(result: Result) {
        callbacks.requireValue.onResult(result)
    }

    @MainThread
    protected fun publish(label: Label) {
        callbacks.requireValue.onLabel(label)
    }
}
