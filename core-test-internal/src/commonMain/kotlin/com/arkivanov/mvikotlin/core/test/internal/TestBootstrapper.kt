package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean

class TestBootstrapper(
    private val init: () -> Unit = {},
    private val invoke: TestBootstrapper.() -> Unit = {}
) : Bootstrapper<String> {

    private val actionConsumer = lazyAtomicReference<(String) -> Unit>()
    val isInitialized: Boolean get() = actionConsumer.value != null

    private val _isDisposed = AtomicBoolean()
    val isDisposed: Boolean get() = _isDisposed.value

    override fun init(actionConsumer: (String) -> Unit) {
        check(this.actionConsumer.value == null) { "Executor is already initialized" }

        this.actionConsumer.value = actionConsumer
        init()
    }

    override fun invoke() {
        invoke.invoke(this)
    }

    override fun dispose() {
        _isDisposed.value = true
    }

    fun dispatch(action: String) {
        actionConsumer.requireValue.invoke(action)
    }
}
