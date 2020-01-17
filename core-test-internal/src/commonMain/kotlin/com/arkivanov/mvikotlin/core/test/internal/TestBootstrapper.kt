package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean

class TestBootstrapper(
    private val bootstrap: TestBootstrapper.() -> Unit = {}
) : Bootstrapper<String> {

    private val actionConsumer = lazyAtomicReference<(String) -> Unit>()

    private val _isDisposed = AtomicBoolean()
    val isDisposed: Boolean get() = _isDisposed.value

    override fun bootstrap(actionConsumer: (String) -> Unit) {
        this.actionConsumer.value = actionConsumer
        bootstrap.invoke(this)
    }

    override fun dispose() {
        _isDisposed.value = true
    }

    fun dispatch(action: String) {
        actionConsumer.requireValue.invoke(action)
    }
}
