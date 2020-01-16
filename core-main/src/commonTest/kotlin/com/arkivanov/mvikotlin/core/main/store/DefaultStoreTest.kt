package com.arkivanov.mvikotlin.core.main.store

import com.arkivanov.mvikotlin.core.internal.rx.observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.arkivanov.mvikotlin.utils.internal.plusAssign
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.freeze
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultStoreTest {

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun state_val_returns_initial_state_WHEN_created() {
        val store = store(initialState = "initial")

        val state = store.state

        assertEquals("initial", state)
    }

    @Test
    fun calls_bootstrapper_WHEN_created() {
        val isCalled = AtomicBoolean()

        store(bootstrapper = TestBootstrapper(bootstrap = { isCalled.value = true }))

        assertTrue(isCalled.value)
    }

    @Test
    fun initializes_executor_WHEN_with_bootstrapper_and_created() {
        val executor = TestExecutor()

        store(bootstrapper = TestBootstrapper(), executorFactory = { executor })

        assertTrue(executor.isInitialized)
    }

    @Test
    fun initializes_executor_WHEN_without_bootstrapper_and_created() {
        val executor = TestExecutor()

        store(executorFactory = { executor })

        assertTrue(executor.isInitialized)
    }

    @Test
    fun initializes_executor_before_bootstrapper_call_WHEN_with_bootstrapper_and_created() {
        val events = AtomicList<String>()

        store(
            bootstrapper = TestBootstrapper { events += "bootstrap" },
            executorFactory = { TestExecutor(init = { _, _, _ -> events += "init" }) }
        )

        assertEquals(listOf("init", "bootstrap"), events.value)
    }

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_after_bootstrap() {
        val actions = AtomicList<String>()
        val bootstrapper = TestBootstrapper()

        store(
            bootstrapper = bootstrapper,
            executorFactory = { TestExecutor(handleAction = { actions += it }) }
        )

        bootstrapper.dispatch("action1")
        bootstrapper.dispatch("action2")

        assertEquals(listOf("action1", "action2"), actions.value)
    }

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_during_bootstrap() {
        val actions = AtomicList<String>()

        store(
            bootstrapper = TestBootstrapper {
                dispatch("action1")
                dispatch("action2")
            },
            executorFactory = { TestExecutor(handleAction = { actions += it }) }
        )

        assertEquals(listOf("action1", "action2"), actions.value)
    }

    @Test
    fun does_not_deliver_actions_from_bootstrapper_to_executor_WHEN_disposed_and_bootstrapper_produced_actions() {
        val actions = AtomicList<String>()
        val bootstrapper = TestBootstrapper()

        val store =
            store(
                bootstrapper = bootstrapper,
                executorFactory = { TestExecutor(handleAction = { actions += it }) }
            )

        store.dispose()
        bootstrapper.dispatch("action1")
        bootstrapper.dispatch("action2")

        assertEquals(emptyList(), actions.value)
    }

    @Test
    fun produces_labels_from_executor() {
        val labels = AtomicList<String>()
        val executor = TestExecutor()

        val store = store(executorFactory = { executor })
        store.labels(observer(onNext = labels::add))

        executor.publish("label1")
        executor.publish("label2")

        assertEquals(listOf("label1", "label2"), labels.value)
    }

    @Test
    fun does_not_produce_labels_from_executor_to_unsubscribed_observer() {
        val labels = AtomicList<String>()
        val executor = TestExecutor()

        val store = store(executorFactory = { executor })
        store.labels(observer(onNext = labels::add)).dispose()

        executor.publish("label1")
        executor.publish("label2")

        assertEquals(emptyList(), labels.value)
    }

    @Test
    fun delivers_intents_to_executor() {
        val intents = AtomicList<String>()
        val store = store(executorFactory = { TestExecutor(handleIntent = { intents += it }) })

        store.accept("intent1")
        store.accept("intent2")

        assertEquals(listOf("intent1", "intent2"), intents.value)
    }

    @Test
    fun does_not_deliver_intents_to_executor_WHEN_disposed_and_new_intents() {
        val intents = AtomicList<String>()
        val store = store(executorFactory = { TestExecutor(handleIntent = { intents += it }) })

        store.dispose()
        store.accept("intent1")
        store.accept("intent2")

        assertEquals(emptyList(), intents.value)
    }

    @Test
    fun executor_can_read_initial_state() {
        val executor = TestExecutor()
        store(initialState = "initial", executorFactory = { executor })

        assertEquals("initial", executor.state)
    }

    @Test
    fun executor_can_read_new_state_WHEN_state_changed() {
        val executor = TestExecutor()
        store(executorFactory = { executor }, reducer = reducer { it })

        executor.dispatch("result")

        assertEquals("result", executor.state)
    }

    @Test
    fun delivers_results_from_executor_to_reducer() {
        val results = AtomicList<String>()
        val executor = TestExecutor()
        store(
            executorFactory = { executor },
            reducer = reducer {
                results += it
                this
            }
        )

        executor.dispatch("result1")
        executor.dispatch("result2")

        assertEquals(listOf("result1", "result2"), results.value)
    }

    @Test
    fun state_val_returns_new_state_WHEN_new_state_returned_from_reducer() {
        val executor = TestExecutor()
        val store =
            store(
                executorFactory = { executor },
                reducer = reducer { it }
            )

        executor.dispatch("result")

        assertEquals("result", store.state)
    }

    @Test
    fun executor_can_read_new_state_WHEN_new_state_returned_from_reducer() {
        val executor = TestExecutor()
        store(
            executorFactory = { executor },
            reducer = reducer { it }
        )

        executor.dispatch("result")

        assertEquals("result", executor.state)
    }

    @Test
    fun bootstrapper_disposed_WHEN_store_disposed() {
        val bootstrapper = TestBootstrapper()
        val store = store(bootstrapper = bootstrapper)

        store.dispose()

        assertTrue(bootstrapper.isDisposed)
    }

    @Test
    fun executor_disposed_WHEN_store_disposed() {
        val executor = TestExecutor()
        val store = store(executorFactory = { executor })

        store.dispose()

        assertTrue(executor.isDisposed)
    }

    @Test
    fun states_observers_completed_WHEN_store_disposed() {
        val isCompleted1 = AtomicBoolean()
        val isCompleted2 = AtomicBoolean()
        val store = store()
        store.states(observer(onComplete = { isCompleted1.value = true }))
        store.states(observer(onComplete = { isCompleted2.value = true }))

        store.dispose()

        assertTrue(isCompleted1.value)
        assertTrue(isCompleted2.value)
    }

    @Test
    fun labels_observers_completed_WHEN_store_disposed() {
        val isCompleted1 = AtomicBoolean()
        val isCompleted2 = AtomicBoolean()
        val store = store()
        store.labels(observer(onComplete = { isCompleted1.value = true }))
        store.labels(observer(onComplete = { isCompleted2.value = true }))

        store.dispose()

        assertTrue(isCompleted1.value)
        assertTrue(isCompleted2.value)
    }

    @Test
    fun states_observers_disposables_disposed_WHEN_store_disposed() {
        val store = store()
        val disposable1 = store.states(observer())
        val disposable2 = store.states(observer())

        store.dispose()

        assertTrue(disposable1.isDisposed)
        assertTrue(disposable2.isDisposed)
    }

    @Test
    fun labels_observers_disposables_disposed_WHEN_store_disposed() {
        val store = store()
        val disposable1 = store.labels(observer())
        val disposable2 = store.labels(observer())

        store.dispose()

        assertTrue(disposable1.isDisposed)
        assertTrue(disposable2.isDisposed)
    }

    @Test
    fun store_isDisposed_returns_true_WHEN_store_disposed() {
        val store = store()

        store.dispose()

        assertTrue(store.isDisposed)
    }

    @Test
    fun executor_can_read_new_state_WHEN_recursive_intent_on_label() {
        val stateRef = lazyAtomicReference<String>()

        val store =
            store(
                executorFactory = {
                    TestExecutor(
                        handleIntent = { intent ->
                            when (intent) {
                                "intent1" -> {
                                    dispatch("result")
                                    publish("label")
                                }
                                "intent2" -> stateRef.value = state
                            }
                        }
                    )
                },
                reducer = reducer { it }
            )

        store.labels(observer(onNext = { store.accept("intent2") }))
        store.accept("intent1")

        assertEquals("result", stateRef.requireValue)
    }

    @Test
    fun executor_can_read_new_state_WHEN_recursive_intent_on_state() {
        val stateRef = lazyAtomicReference<String>()

        val store =
            store(
                executorFactory = {
                    TestExecutor(
                        handleIntent = { intent ->
                            when (intent) {
                                "intent1" -> dispatch("result")
                                "intent2" -> stateRef.value = state
                            }
                        }
                    )
                },
                reducer = reducer { it }
            )

        store.states(
            observer(
                onNext = {
                    if (it == "result") {
                        store.accept("intent2")
                    }
                }
            )
        )
        store.accept("intent1")

        assertEquals("result", stateRef.requireValue)
    }

    private fun store(
        initialState: String = "initial_state",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer()
    ): DefaultStore<String, String, String, String, String> =
        DefaultStore(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        ).freeze()

    private fun reducer(reduce: String.(String) -> String = { it }): Reducer<String, String> =
        object : Reducer<String, String> {
            override fun String.reduce(result: String): String = reduce(result)
        }

    private class TestBootstrapper(
        private val bootstrap: TestBootstrapper.() -> Unit = {}
    ) : Bootstrapper<String> {
        private val actionConsumer = lazyAtomicReference<(String) -> Unit>()

        private val _isDisposed = AtomicBoolean()
        val isDisposed: Boolean get() = _isDisposed.value

        override fun bootstrap(actionConsumer: (String) -> Unit) {
            this.actionConsumer.value = actionConsumer
            bootstrap.invoke(this)
        }

        override fun dispose() {
            _isDisposed.value = true
        }

        fun dispatch(action: String) {
            actionConsumer.requireValue.invoke(action)
        }
    }

    private class TestExecutor(
        private val init: (
            stateSupplier: () -> String,
            resultConsumer: (String) -> Unit,
            labelConsumer: (String) -> Unit
        ) -> Unit = { _, _, _ -> Unit },
        private val handleIntent: TestExecutor.(String) -> Unit = {},
        private val handleAction: TestExecutor.(String) -> Unit = {}
    ) : Executor<String, String, String, String, String> {
        private val stateSupplier = lazyAtomicReference<() -> String>()
        private val resultConsumer = lazyAtomicReference<(String) -> Unit>()
        private val labelConsumer = lazyAtomicReference<(String) -> Unit>()

        val isInitialized: Boolean get() = stateSupplier.value != null

        val state: String get() = stateSupplier.requireValue.invoke()

        private val _isDisposed = AtomicBoolean()
        val isDisposed: Boolean get() = _isDisposed.value

        override fun init(stateSupplier: () -> String, resultConsumer: (String) -> Unit, labelConsumer: (String) -> Unit) {
            this.stateSupplier.value = stateSupplier
            this.resultConsumer.value = resultConsumer
            this.labelConsumer.value = labelConsumer
            this.init.invoke(stateSupplier, resultConsumer, labelConsumer)
        }

        override fun handleIntent(intent: String) {
            handleIntent.invoke(this, intent)
        }

        override fun handleAction(action: String) {
            handleAction.invoke(this, action)
        }

        override fun dispose() {
            _isDisposed.value = true
        }

        fun dispatch(result: String) {
            resultConsumer.requireValue.invoke(result)
        }

        fun publish(label: String) {
            labelConsumer.requireValue.invoke(label)
        }
    }
}
