package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean

class TestExecutor(
    private val init: (
        stateSupplier: () -> String,
        resultConsumer: (String) -> Unit,
        labelConsumer: (String) -> Unit
    ) -> Unit = { _, _, _ -> Unit },
    private val handleIntent: TestExecutor.(String) -> Unit = {},
    private val handleAction: TestExecutor.(String) -> Unit = {}
) : Executor<String, String, String, String, String> {

    private val stateSupplier = lazyAtomicReference<() -> String>()
    private val resultConsumer = lazyAtomicReference<(String) -> Unit>()
    private val labelConsumer = lazyAtomicReference<(String) -> Unit>()

    val isInitialized: Boolean get() = stateSupplier.value != null

    val state: String get() = stateSupplier.requireValue.invoke()

    private val _isDisposed = AtomicBoolean()
    val isDisposed: Boolean get() = _isDisposed.value

    override fun init(stateSupplier: () -> String, resultConsumer: (String) -> Unit, labelConsumer: (String) -> Unit) {
        this.stateSupplier.value = stateSupplier
        this.resultConsumer.value = resultConsumer
        this.labelConsumer.value = labelConsumer
        this.init.invoke(stateSupplier, resultConsumer, labelConsumer)
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
        resultConsumer.requireValue.invoke(result)
    }

    fun publish(label: String) {
        labelConsumer.requireValue.invoke(label)
    }
}
