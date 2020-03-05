package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.LoggingMode
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.logger.log

internal class LoggingExecutor<in Intent, in Action, State, Result, Label>(
    private val delegate: Executor<Intent, Action, State, Result, Label>,
    private val logger: Logger,
    private val loggingMode: () -> LoggingMode,
    private val storeName: String
) : Executor<Intent, Action, State, Result, Label> by delegate {

    override fun init(callbacks: Executor.Callbacks<State, Result, Label>) {
        delegate.init(
            object : Executor.Callbacks<State, Result, Label> {
                override val state: State get() = callbacks.state

                override fun onResult(result: Result) {
                    logger.log(loggingMode(), storeName, StoreEventType.RESULT, result)
                    callbacks.onResult(result)
                }

                override fun onLabel(label: Label) {
                    logger.log(loggingMode(), storeName, StoreEventType.LABEL, label)
                    callbacks.onLabel(label)
                }
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
