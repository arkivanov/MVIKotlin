package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.test.internal.TestBootstrapper
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import com.arkivanov.mvikotlin.logging.logger.LogFormatter
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.freeze
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggingStoreFactoryTest {

    private val formatter = TestLogFormatter()
    private val logger = TestLogger(formatter)

    @Test
    fun logs_created() {
        store()

        logger.assertLoggedText("$STORE_NAME: created")
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
    fun logs_results() {
        val executor = TestExecutor()
        store(executorFactory = { executor }, reducer = reducer { "new_state" })

        executor.dispatch("some_result")

        logger.assertLoggedEvent(StoreEventType.RESULT, "some_result")
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

        executor.dispatch("some_result")

        logger.assertLoggedEvent(StoreEventType.STATE, "initial_state_some_result")
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
        initialState: String = "initial",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer()
    ): Store<String, String, String> {
        val delegate =
            object : StoreFactory {
                override fun <Intent : Any, Action : Any, Result : Any, State : Any, Label : Any> create(
                    name: String?,
                    initialState: State,
                    bootstrapper: Bootstrapper<Action>?,
                    executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
                    reducer: Reducer<State, Result>
                ): Store<Intent, State, Label> =
                    TestStore(
                        initialState = initialState,
                        bootstrapper = bootstrapper,
                        executorFactory = executorFactory,
                        reducer = reducer
                    )
            }

        val factory = LoggingStoreFactory(delegate = delegate, logger = logger, logFormatter = formatter)

        return factory.create(
            name = name,
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        )
    }

    private companion object {
        private const val STORE_NAME = "store"
    }

    private class TestLogger(
        private val formatter: LogFormatter
    ) : Logger {
        private val logs = AtomicList<String>()

        override fun log(text: String) {
            logs += text
        }

        fun assertLoggedText(text: String) {
            assertTrue(text in logs.value)
        }

        fun assertLoggedEvent(eventType: StoreEventType, value: Any?) {
            assertLoggedText(requireNotNull(formatter.format(storeName = STORE_NAME, eventType = eventType, value = value)))
        }

        fun assertNoLogs() {
            assertEquals(emptyList(), logs.value)
        }
    }

    private class TestLogFormatter : LogFormatter {
        override fun format(storeName: String, eventType: StoreEventType, value: Any?): String? =
            "$storeName;$eventType;$value"
    }

    private class TestStore<in Intent : Any, Action : Any, out State : Any, Result : Any, Label : Any>(
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
        private val reducer: Reducer<State, Result>
    ) : Store<Intent, State, Label> {
        private val _state = AtomicReference(initialState)
        override val state: State get() = _state.value
        override val isDisposed: Boolean = false
        private val executor = executorFactory()

        init {
            freeze()

            executor.init(
                object : Executor.Callbacks<State, Result, Label> {
                    override val state: State get() = _state.value

                    override fun onResult(result: Result) {
                        _state.update { oldState ->
                            reducer.run {
                                oldState.reduce(result)
                            }
                        }
                    }

                    override fun onLabel(label: Label) {
                        // no-op
                    }
                }
            )

            bootstrapper?.init(executor::handleAction)
            bootstrapper?.invoke()
        }

        override fun states(observer: Observer<State>): Disposable = throw NotImplementedError("Not required")

        override fun labels(observer: Observer<Label>): Disposable = throw NotImplementedError("Not required")

        override fun accept(intent: Intent) {
            executor.handleIntent(intent)
        }

        override fun dispose() {
        }
    }
}
