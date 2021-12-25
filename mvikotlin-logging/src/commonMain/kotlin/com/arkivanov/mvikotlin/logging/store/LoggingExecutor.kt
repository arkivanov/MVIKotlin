package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.logger.LoggerWrapper
import com.arkivanov.mvikotlin.logging.logger.log

internal class LoggingExecutor<in Intent : Any, in Action : Any, State : Any, Message : Any, Label : Any>(
    private val delegate: Executor<Intent, Action, State, Message, Label>,
    private val logger: LoggerWrapper,
    private val storeName: String
) : Executor<Intent, Action, State, Message, Label> by delegate {

    override fun init(callbacks: Executor.Callbacks<State, Message, Label>) {
        delegate.init(
            object : Executor.Callbacks<State, Message, Label> {
                override val state: State get() = callbacks.state

                override fun onMessage(message: Message) {
                    logger.log(storeName = storeName, eventType = StoreEventType.MESSAGE, value = message)
                    callbacks.onMessage(message)
                }

                override fun onLabel(label: Label) {
                    logger.log(storeName = storeName, eventType = StoreEventType.LABEL, value = label)
                    callbacks.onLabel(label)
                }
            }
        )
    }

    override fun executeAction(action: Action) {
        logger.log(storeName = storeName, eventType = StoreEventType.ACTION, value = action)
        delegate.executeAction(action)
    }

    override fun executeIntent(intent: Intent) {
        logger.log(storeName = storeName, eventType = StoreEventType.INTENT, value = intent)
        delegate.executeIntent(intent)
    }
}
