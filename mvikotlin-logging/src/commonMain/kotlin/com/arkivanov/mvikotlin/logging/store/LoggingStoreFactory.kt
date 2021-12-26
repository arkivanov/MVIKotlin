package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.DefaultLogFormatter
import com.arkivanov.mvikotlin.logging.logger.DefaultLogger
import com.arkivanov.mvikotlin.logging.logger.LogFormatter
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.logger.LoggerWrapper

/**
 * An implementation of the [StoreFactory] that wraps another [StoreFactory] and provides logging
 *
 * @param delegate a [StoreFactory] that will be wrapped by this factory
 * @param logger a [Logger], by default the [DefaultLogger] is used
 * @param logFormatter a [LogFormatter], by default the [DefaultLogFormatter] with default arguments is used
 */
class LoggingStoreFactory(
    private val delegate: StoreFactory,
    logger: Logger = DefaultLogger,
    logFormatter: LogFormatter = DefaultLogFormatter()
) : StoreFactory by delegate {

    constructor(delegate: StoreFactory) : this(delegate, DefaultLogger, DefaultLogFormatter())

    private val loggerWrapper = LoggerWrapper(logger, logFormatter)

    override fun <Intent : Any, Action : Any, Message : Any, State : Any, Label : Any> create(
        name: String?,
        isAutoInit: Boolean,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
        reducer: Reducer<State, Message>
    ): Store<Intent, State, Label> {
        if (name == null) {
            return delegate.create(
                isAutoInit = isAutoInit,
                initialState = initialState,
                bootstrapper = bootstrapper,
                executorFactory = executorFactory,
                reducer = reducer
            )
        }

        loggerWrapper.log("$name: creating")

        val delegateStore =
            delegate.create(
                name = name,
                isAutoInit = false,
                initialState = initialState,
                bootstrapper = bootstrapper,
                executorFactory = {
                    LoggingExecutor(
                        delegate = executorFactory(),
                        logger = loggerWrapper,
                        storeName = name
                    )
                },
                reducer = LoggingReducer(
                    delegate = reducer,
                    logger = loggerWrapper,
                    storeName = name
                )
            )

        return LoggingStore(
            delegate = delegateStore,
            logger = loggerWrapper,
            name = name
        ).apply {
            if (isAutoInit) {
                init()
            }
        }
    }
}
