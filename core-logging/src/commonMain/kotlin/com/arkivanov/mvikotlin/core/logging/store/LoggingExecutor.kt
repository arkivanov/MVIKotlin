package com.arkivanov.mvikotlin.core.logging.store

import com.arkivanov.mvikotlin.core.logging.LoggingMode
import com.arkivanov.mvikotlin.core.logging.logger.Logger
import com.arkivanov.mvikotlin.core.logging.logger.log
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreEventType

internal class LoggingExecutor<in Intent, in Action, in State, out Result, out Label>(
    private val delegate: Executor<Intent, Action, State, Result, Label>,
    private val logger: Logger,
    private val loggingMode: () -> LoggingMode,
    private val storeName: String
) : Executor<Intent, Action, State, Result, Label> by delegate {

    override fun init(stateSupplier: () -> State, resultConsumer: (Result) -> Unit, labelConsumer: (Label) -> Unit) {
        delegate.init(
            stateSupplier = stateSupplier,
            resultConsumer = { result ->
                logger.log(loggingMode(), storeName, StoreEventType.RESULT, result)
                resultConsumer(result)
            },
            labelConsumer = { label ->
                logger.log(loggingMode(), storeName, StoreEventType.LABEL, label)
                labelConsumer(label)
            }
        )
    }

    override fun handleAction(action: Action) {
        logger.log(loggingMode(), storeName, StoreEventType.ACTION, action)
        delegate.handleAction(action)
    }

    override fun handleIntent(intent: Intent) {
        logger.log(loggingMode(), storeName, StoreEventType.INTENT, intent)
        delegate.handleIntent(intent)
    }
}
