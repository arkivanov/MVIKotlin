package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Executor.Callbacks
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue

abstract class BaseExecutor<in Intent, in Action, Result, State, Label> : Executor<Intent, Action, Result, State, Label> {

    private val callbacks = lazyAtomicReference<Callbacks<State, Result, Label>>()
    protected val state: State get() = callbacks.requireValue.state

    final override fun init(callbacks: Callbacks<State, Result, Label>) {
        check(this.callbacks.value == null) { "Executor is already initialized" }

        this.callbacks.value = callbacks
    }

    override fun handleIntent(intent: Intent) {
    }

    override fun handleAction(action: Action) {
    }

    override fun dispose() {
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
