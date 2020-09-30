package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.arkivanov.mvikotlin.utils.internal.setValue

class TestBootstrapper(
    private val init: () -> Unit = {},
    private val invoke: TestBootstrapper.() -> Unit = {}
) : Bootstrapper<String> {

    private val actionConsumer = atomic<(String) -> Unit>()
    val isInitialized: Boolean get() = actionConsumer.value != null

    var isDisposed: Boolean by atomic(false)
        private set

    override fun init(actionConsumer: (String) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
        init()
    }

    override fun invoke() {
        invoke.invoke(this)
    }

    override fun dispose() {
        isDisposed = true
    }

    fun dispatch(action: String) {
        actionConsumer.requireValue().invoke(action)
    }
}
