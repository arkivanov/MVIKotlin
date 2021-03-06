package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Executor.Callbacks
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.arkivanov.mvikotlin.utils.internal.setValue

class TestExecutor(
    private val init: () -> Unit = {},
    private val handleIntent: TestExecutor.(String) -> Unit = {},
    private val handleAction: TestExecutor.(String) -> Unit = {}
) : Executor<String, String, String, String, String> {

    private val callbacks = atomic<Callbacks<String, String, String>>()
    val isInitialized: Boolean get() = callbacks.value != null
    val state: String get() = callbacks.requireValue().state

    var isDisposed: Boolean by atomic(false)
        private set

    override fun init(callbacks: Callbacks<String, String, String>) {
        this.callbacks.initialize(callbacks)
        init()
    }

    override fun handleIntent(intent: String) {
        handleIntent.invoke(this, intent)
    }

    override fun handleAction(action: String) {
        handleAction.invoke(this, action)
    }

    override fun dispose() {
        isDisposed = true
    }

    fun dispatch(result: String) {
        callbacks.requireValue().onResult(result)
    }

    fun publish(label: String) {
        callbacks.requireValue().onLabel(label)
    }
}
