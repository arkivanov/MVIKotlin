package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.logging.LoggingMode
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.logger.log

internal class LoggingStore<in Intent : Any, out State : Any, out Label : Any>(
    private val delegate: Store<Intent, State, Label>,
    private val logger: Logger,
    private val loggingMode: () -> LoggingMode,
    private val name: String
) : Store<Intent, State, Label> by delegate {

    override fun dispose() {
        delegate.dispose()
        logger.log(loggingMode()) { "$name: disposed" }
    }
}
