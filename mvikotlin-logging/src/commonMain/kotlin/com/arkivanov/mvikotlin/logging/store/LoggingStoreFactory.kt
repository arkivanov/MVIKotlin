package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.LoggingMode
import com.arkivanov.mvikotlin.logging.logger.DefaultLogger
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.logger.LoggerWrapper
import com.arkivanov.mvikotlin.logging.logger.log

/**
 * An implementation of the [StoreFactory] that wraps another [StoreFactory] and provides logging
 *
 * @param delegate a [StoreFactory] that will be wrapped by this factory
 * @param logger a [Logger], by default the [DefaultLogger] is used
 * @param mode logging mode, see [LoggingMode] for more information
 */
class LoggingStoreFactory(
    private val delegate: StoreFactory,
    var logger: Logger = DefaultLogger,
    var mode: LoggingMode = LoggingMode.MEDIUM,
    var eventTypes: Set<StoreEventType> = StoreEventType.ALL
) : StoreFactory {

    private val loggerWrapper: LoggerWrapper =
        object : LoggerWrapper {
            override val mode: LoggingMode get() = this@LoggingStoreFactory.mode
            override val eventTypes: Set<StoreEventType> get() = this@LoggingStoreFactory.eventTypes

            override fun log(text: String) {
                this@LoggingStoreFactory.logger.log(text)
            }
        }

    override fun <Intent : Any, Action : Any, Result : Any, State : Any, Label : Any> create(
        name: String?,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
        reducer: Reducer<State, Result>
    ): Store<Intent, State, Label> {
        if (name == null) {
            return delegate.create(
                initialState = initialState,
                bootstrapper = bootstrapper,
                executorFactory = executorFactory,
                reducer = reducer
            )
        }

        loggerWrapper.log { "$name: created" }

        val delegateStore =
            delegate.create(
                name = name,
                initialState = initialState,
                bootstrapper = bootstrapper,
                executorFactory = { executorFactory().wrap(name) },
                reducer = reducer.wrap(name)
            )

        return LoggingStore(
            delegate = delegateStore,
            logger = loggerWrapper,
            name = name
        )
    }

    private fun <Intent : Any, Action : Any, State : Any, Result : Any, Label : Any> Executor<Intent, Action, State, Result, Label>.wrap(
        storeName: String
    ): Executor<Intent, Action, State, Result, Label> =
        LoggingExecutor(
            delegate = this,
            logger = loggerWrapper,
            storeName = storeName
        )

    private fun <State : Any, Result : Any> Reducer<State, Result>.wrap(storeName: String): Reducer<State, Result> =
        LoggingReducer(
            delegate = this,
            logger = loggerWrapper,
            storeName = storeName
        )
}
