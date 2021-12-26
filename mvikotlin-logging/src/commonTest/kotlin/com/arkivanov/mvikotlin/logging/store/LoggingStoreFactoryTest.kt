package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.test.internal.TestBootstrapper
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import kotlin.test.Test

class LoggingStoreFactoryTest {

    private val formatter = TestLogFormatter()
    private val logger = TestLogger(formatter = formatter, storeName = STORE_NAME)

    @Test
    fun logs_created() {
        store()

        logger.assertLoggedText("$STORE_NAME: creating")
    }

    @Test
    fun logs_initializing_WHEN_isAutoInit_true() {
        store(isAutoInit = true)

        logger.assertLoggedText("$STORE_NAME: initializing")
    }

    @Test
    fun does_not_log_initializing_WHEN_isAutoInit_false() {
        store(isAutoInit = false)

        logger.assertNoLoggedText("$STORE_NAME: initializing")
    }

    @Test
    fun logs_initializing_WHEN_isAutoInit_false_and_init_called() {
        val store = store(isAutoInit = false)

        store.init()

        logger.assertLoggedText("$STORE_NAME: initializing")
    }

    @Test
    fun logs_disposed() {
        store().dispose()

        logger.assertLoggedText("$STORE_NAME: disposed")
    }

    @Test
    fun logs_intents() {
        val store = store()

        store.accept("some_intent")

        logger.assertLoggedEvent(StoreEventType.INTENT, "some_intent")
    }

    @Test
    fun logs_actions() {
        val bootstrapper = TestBootstrapper()
        store(bootstrapper = bootstrapper)

        bootstrapper.dispatch("some_action")

        logger.assertLoggedEvent(StoreEventType.ACTION, "some_action")
    }

    @Test
    fun logs_messages() {
        val executor = TestExecutor()
        store(executorFactory = { executor }, reducer = reducer { "new_state" })

        executor.dispatch("some_message")

        logger.assertLoggedEvent(StoreEventType.MESSAGE, "some_message")
    }

    @Test
    fun logs_labels() {
        val executor = TestExecutor()
        store(executorFactory = { executor })

        executor.publish("some_label")

        logger.assertLoggedEvent(StoreEventType.LABEL, "some_label")
    }

    @Test
    fun logs_states() {
        val executor = TestExecutor()
        store(initialState = "initial_state", executorFactory = { executor }, reducer = reducer { "${this}_$it" })

        executor.dispatch("some_message")

        logger.assertLoggedEvent(StoreEventType.STATE, "initial_state_some_message")
    }

    @Test
    fun does_not_log_without_name() {
        val bootstrapper = TestBootstrapper()
        val executor = TestExecutor()

        val store =
            store(
                name = null,
                initialState = "initial_state",
                bootstrapper = bootstrapper,
                executorFactory = { executor },
                reducer = reducer { this }
            )

        bootstrapper.dispatch("action")
        store.accept("intent")
        store.dispose()

        logger.assertNoLogs()
    }

    private fun store(
        name: String? = STORE_NAME,
        isAutoInit: Boolean = true,
        initialState: String = "initial",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer()
    ): Store<String, String, String> {
        val factory = LoggingStoreFactory(delegate = TestStoreFactory, logger = logger, logFormatter = formatter)

        return factory.create(
            name = name,
            isAutoInit = isAutoInit,
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        )
    }

    private companion object {
        private const val STORE_NAME = "store"
    }
}
