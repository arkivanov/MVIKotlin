package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.logging.logger.LoggerWrapper

internal class LoggingStore<in Intent : Any, out State : Any, out Label : Any>(
    private val delegate: Store<Intent, State, Label>,
    private val logger: LoggerWrapper,
    private val name: String
) : Store<Intent, State, Label> by delegate {

    override fun init() {
        logger.log("$name: initializing")
        delegate.init()
    }

    override fun dispose() {
        delegate.dispose()
        logger.log("$name: disposed")
    }
}
