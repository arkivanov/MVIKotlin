package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.LoggingMode
import com.arkivanov.mvikotlin.logging.logger.DefaultLogger
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.logger.log

/**
 * An implementation of [StoreFactory] that wraps another Store factory and provides logging
 *
 * @param delegate an instance of another factory that will be wrapped by this factory
 * @param logger A [Logger] that can be used to implement custom logging. By default [DefaultLogger] is used.
 * @param mode logging mode, see [LoggingMode] for more information
 */
class LoggingStoreFactory(
    private val delegate: StoreFactory,
    private val logger: Logger = DefaultLogger,
    var mode: LoggingMode = LoggingMode.MEDIUM
) : StoreFactory {

    override fun <Intent : Any, Action : Any, Result : Any, State : Any, Label : Any> create(
        name: String,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, Result, State, Label>,
        reducer: Reducer<State, Result>
    ): Store<Intent, State, Label> {
        logger.log(mode) { "$name: created" }

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
            logger = logger,
            loggingMode = ::mode,
            name = name
        )
    }

    private fun <Intent, Action, Result, State, Label> Executor<Intent, Action, Result, State, Label>.wrap(
        storeName: String
    ): Executor<Intent, Action, Result, State, Label> =
        LoggingExecutor(
            delegate = this,
            logger = logger,
            loggingMode = ::mode,
            storeName = storeName
        )

    private fun <State : Any, Result : Any> Reducer<State, Result>.wrap(storeName: String): Reducer<State, Result> =
        LoggingReducer(
            delegate = this,
            logger = logger,
            loggingMode = ::mode,
            storeName = storeName
        )
}
