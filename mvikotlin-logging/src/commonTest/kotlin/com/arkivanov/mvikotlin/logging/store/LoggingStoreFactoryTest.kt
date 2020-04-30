package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.test.internal.TestBootstrapper
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
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

class LoggingStoreFactoryTest {

    private val logger = TestLogger()

    @Test
    fun logs_creation() {
        store(logger = logger)

        logger.assertSubString("created")
    }

    @Test
    fun logs_disposal() {
        store(logger = logger).dispose()

        logger.assertSubString("disposed")
    }

    @Test
    fun logs_intents() {
        val store = store(logger = logger)

        store.accept("some_intent")

        logger.assertSubString("some_intent")
    }

    @Test
    fun logs_actions() {
        val bootstrapper = TestBootstrapper()
        store(bootstrapper = bootstrapper, logger = logger)

        bootstrapper.dispatch("some_action")

        logger.assertSubString("some_action")
    }

    @Test
    fun logs_results() {
        val executor = TestExecutor()
        store(executorFactory = { executor }, reducer = reducer { "new_state" }, logger = logger)

        executor.dispatch("some_result")

        logger.assertSubString("some_result")
    }

    @Test
    fun logs_labels() {
        val executor = TestExecutor()
        store(executorFactory = { executor }, logger = logger)

        executor.publish("some_label")

        logger.assertSubString("some_label")
    }

    @Test
    fun logs_states() {
        val executor = TestExecutor()
        store(initialState = "initial_state", executorFactory = { executor }, reducer = reducer { "${this}_$it" }, logger = logger)

        executor.dispatch("some_result")

        logger.assertSubString("initial_state_some_result")
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
                reducer = reducer { this },
                logger = logger
            )

        bootstrapper.dispatch("action")
        store.accept("intent")

        logger.assertNoLogs()
    }

    private fun store(
        name: String? = "name",
        initialState: String = "initial",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer(),
        logger: Logger
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

        val factory = LoggingStoreFactory(delegate = delegate, logger = logger)

        return factory.create(
            name = name,
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        )
    }

    private class TestLogger : Logger {
        private val logs = AtomicList<String>()

        override fun log(text: String) {
            logs += text
        }

        fun assertSubString(text: String) {
            assertEquals(1, logs.value.count { it.contains(text) })
        }

        fun assertNoLogs() {
            assertEquals(emptyList(), logs.value)
        }
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
