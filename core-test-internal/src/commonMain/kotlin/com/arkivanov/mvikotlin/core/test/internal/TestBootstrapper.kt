package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean

class TestBootstrapper(
    private val init: () -> Unit = {},
    private val invoke: TestBootstrapper.() -> Unit = {}
) : Bootstrapper<String> {

    private val actionConsumer = lateinitAtomicReference<(String) -> Unit>()
    val isInitialized: Boolean get() = actionConsumer.value != null

    private val _isDisposed = AtomicBoolean()
    val isDisposed: Boolean get() = _isDisposed.value

    override fun init(actionConsumer: (String) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
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
