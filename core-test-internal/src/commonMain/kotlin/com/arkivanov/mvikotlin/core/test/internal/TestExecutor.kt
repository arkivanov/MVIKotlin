package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Executor.Callbacks
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean

class TestExecutor(
    private val init: (Callbacks<String, String, String>) -> Unit = {},
    private val handleIntent: TestExecutor.(String) -> Unit = {},
    private val handleAction: TestExecutor.(String) -> Unit = {}
) : Executor<String, String, String, String, String> {

    private val callbacks = lazyAtomicReference<Callbacks<String, String, String>>()
    val isInitialized: Boolean get() = callbacks.value != null
    val state: String get() = callbacks.requireValue.state
    private val _isDisposed = AtomicBoolean()
    val isDisposed: Boolean get() = _isDisposed.value

    override fun init(callbacks: Callbacks<String, String, String>) {
        check(this.callbacks.value == null) { "Executor is already initialized" }

        this.callbacks.value = callbacks
        init.invoke(callbacks)
    }

    override fun handleIntent(intent: String) {
        handleIntent.invoke(this, intent)
    }

    override fun handleAction(action: String) {
        handleAction.invoke(this, action)
    }

    override fun dispose() {
        _isDisposed.value = true
    }

    fun dispatch(result: String) {
        callbacks.requireValue.onResult(result)
    }

    fun publish(label: String) {
        callbacks.requireValue.onLabel(label)
    }
}
