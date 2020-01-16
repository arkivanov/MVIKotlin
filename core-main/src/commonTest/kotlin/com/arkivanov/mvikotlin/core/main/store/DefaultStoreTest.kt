package com.arkivanov.mvikotlin.core.main.store

import com.arkivanov.mvikotlin.core.internal.rx.observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.arkivanov.mvikotlin.utils.internal.plusAssign
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

        store(bootstrapper = bootstrapper(bootstrap = { isCalled.value = true }))

        assertTrue(isCalled.value)
    }

    @Test
    fun initializes_executor_WHEN_with_bootstrapper_and_created() {
        val isInitialized = AtomicBoolean()

        store(
            bootstrapper = bootstrapper(),
            executorFactory = executor(init = { _, _, _ -> isInitialized.value = true }).factory()
        )

        assertTrue(isInitialized.value)
    }

    @Test
    fun initializes_executor_WHEN_without_bootstrapper_and_created() {
        val isInitialized = AtomicBoolean()

        store(executorFactory = executor(init = { _, _, _ -> isInitialized.value = true }).factory())

        assertTrue(isInitialized.value)
    }

    @Test
    fun initializes_executor_before_bootstrapper_call_WHEN_with_bootstrapper_and_created() {
        val events = AtomicList<String>()

        store(
            bootstrapper = bootstrapper(bootstrap = { events += "bootstrap" }),
            executorFactory = executor(init = { _, _, _ -> events += "init" }).factory()
        )

        assertEquals(listOf("init", "bootstrap"), events.value)
    }

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_after_bootstrap() {
        val actions = AtomicList<String>()
        lateinit var actionConsumer: (String) -> Unit

        store(
            bootstrapper = bootstrapper(bootstrap = { actionConsumer = it }),
            executorFactory = executor(handleAction = actions::add).factory()
        )

        actionConsumer("action1")
        actionConsumer("action2")

        assertEquals(listOf("action1", "action2"), actions.value)
    }

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_during_bootstrap() {
        val actions = AtomicList<String>()

        store(
            bootstrapper = bootstrapper(
                bootstrap = {
                    it("action1")
                    it("action2")
                }
            ),
            executorFactory = executor(handleAction = actions::add).factory()
        )

        assertEquals(listOf("action1", "action2"), actions.value)
    }

    @Test
    fun does_not_deliver_actions_from_bootstrapper_to_executor_WHEN_disposed_and_bootstrapper_produced_actions() {
        val actions = AtomicList<String>()
        lateinit var actionConsumer: (String) -> Unit

        val store =
            store(
                bootstrapper = bootstrapper(bootstrap = { actionConsumer = it }),
                executorFactory = executor(handleAction = actions::add).factory()
            )

        store.dispose()
        actionConsumer("action1")
        actionConsumer("action2")

        assertEquals(emptyList(), actions.value)
    }

    @Test
    fun produces_labels_from_executor() {
        val labels = AtomicList<String>()
        lateinit var labelConsumer: (String) -> Unit

        val store = store(executorFactory = executor(init = { _, _, it -> labelConsumer = it }).factory())
        store.labels(observer(onNext = labels::add))

        labelConsumer("label1")
        labelConsumer("label2")

        assertEquals(listOf("label1", "label2"), labels.value)
    }

    @Test
    fun does_not_produce_labels_from_executor_to_unsubscribed_observer() {
        val labels = AtomicList<String>()
        lateinit var labelConsumer: (String) -> Unit

        val store = store(executorFactory = executor(init = { _, _, it -> labelConsumer = it }).factory())
        store.labels(observer(onNext = labels::add)).dispose()

        labelConsumer("label1")
        labelConsumer("label2")

        assertEquals(emptyList(), labels.value)
    }

    @Test
    fun delivers_intents_to_executor() {
        val intents = AtomicList<String>()
        val store = store(executorFactory = executor(handleIntent = intents::add).factory())

        store.accept("intent1")
        store.accept("intent2")

        assertEquals(listOf("intent1", "intent2"), intents.value)
    }

    @Test
    fun does_not_deliver_intents_to_executor_WHEN_disposed_and_new_intents() {
        val intents = AtomicList<String>()
        val store = store(executorFactory = executor(handleIntent = intents::add).factory())

        store.dispose()
        store.accept("intent1")
        store.accept("intent2")

        assertEquals(emptyList(), intents.value)
    }

    @Test
    fun executor_can_read_initial_state() {
        lateinit var stateSupplier: () -> String
        store(initialState = "initial", executorFactory = executor(init = { it, _, _ -> stateSupplier = it }).factory())

        val state = stateSupplier()

        assertEquals("initial", state)
    }

    @Test
    fun executor_can_read_new_state_WHEN_state_changed() {
        lateinit var stateSupplier: () -> String
        lateinit var resultConsumer: (String) -> Unit
        store(
            executorFactory = executor(
                init = { stateSupplierFromStore, resultConsumerFromStore, _ ->
                    stateSupplier = stateSupplierFromStore
                    resultConsumer = resultConsumerFromStore
                }
            ).factory(),
            reducer = reducer { it }
        )

        resultConsumer("result")
        val state = stateSupplier()

        assertEquals("result", state)
    }

    @Test
    fun delivers_results_from_executor_to_reducer() {
        val results = AtomicList<String>()
        lateinit var resultConsumer: (String) -> Unit
        store(
            executorFactory = executor(init = { _, it, _ -> resultConsumer = it }).factory(),
            reducer = reducer {
                results += it
                this
            }
        )

        resultConsumer("result1")
        resultConsumer("result2")

        assertEquals(listOf("result1", "result2"), results.value)
    }

    @Test
    fun state_val_returns_new_state_WHEN_new_state_returned_from_reducer() {
        lateinit var resultConsumer: (String) -> Unit
        val store =
            store(
                executorFactory = executor(init = { _, it, _ -> resultConsumer = it }).factory(),
                reducer = reducer { it }
            )

        resultConsumer("result")

        assertEquals("result", store.state)
    }

    @Test
    fun executor_can_read_new_state_W_WHEN_new_state_returned_from_reducer() {
        lateinit var stateSupplier: () -> String
        lateinit var resultConsumer: (String) -> Unit
        store(
            executorFactory = executor(
                init = { stateSupplierFromStore, resultConsumerFromStore, _ ->
                    stateSupplier = stateSupplierFromStore
                    resultConsumer = resultConsumerFromStore
                }
            ).factory(),
            reducer = reducer { it }
        )

        resultConsumer("result")
        val state = stateSupplier()

        assertEquals("result", state)
    }

    @Test
    fun bootstrapper_disposed_WHEN_store_disposed() {
        val isDisposed = AtomicBoolean()
        val store = store(bootstrapper = bootstrapper(dispose = { isDisposed.value = true }))

        store.dispose()

        assertTrue(isDisposed.value)
    }

    @Test
    fun executor_disposed_WHEN_store_disposed() {
        val isDisposed = AtomicBoolean()
        val store = store(executorFactory = executor(dispose = { isDisposed.value = true }).factory())

        store.dispose()

        assertTrue(isDisposed.value)
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
        lateinit var stateSupplier: () -> String
        lateinit var resultConsumer: (String) -> Unit
        lateinit var labelConsumer: (String) -> Unit
        lateinit var state: String
        val store =
            store(
                executorFactory = executor(
                    init = { stateSupplierFromStore, resultConsumerFromStore, labelConsumerFromStore ->
                        stateSupplier = stateSupplierFromStore
                        resultConsumer = resultConsumerFromStore
                        labelConsumer = labelConsumerFromStore
                    },
                    handleIntent = { intent ->
                        when (intent) {
                            "intent1" -> {
                                resultConsumer("result")
                                labelConsumer("label1")
                            }
                            "intent2" -> state = stateSupplier()
                        }
                    }
                ).factory(),
                reducer = reducer { it }
            )

        store.labels(observer(onNext = { store.accept("intent2") }))
        store.accept("intent1")

        assertEquals("result", state)
    }

    @Test
    fun executor_can_read_new_state_WHEN_recursive_intent_on_state() {
        lateinit var stateSupplier: () -> String
        lateinit var resultConsumer: (String) -> Unit
        lateinit var state: String
        val store =
            store(
                executorFactory = executor(
                    init = { stateSupplierFromStore, resultConsumerFromStore, _ ->
                        stateSupplier = stateSupplierFromStore
                        resultConsumer = resultConsumerFromStore
                    },
                    handleIntent = { intent ->
                        when (intent) {
                            "intent1" -> resultConsumer("result")
                            "intent2" -> state = stateSupplier()
                        }
                    }
                ).factory(),
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

        assertEquals("result", state)
    }

    private fun store(
        initialState: String = "initial_state",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { executor() },
        reducer: Reducer<String, String> = reducer()
    ): DefaultStore<String, String, String, String, String> =
        DefaultStore(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        ).freeze()

    private fun bootstrapper(
        bootstrap: (actionConsumer: (String) -> Unit) -> Unit = {},
        dispose: () -> Unit = {}
    ): Bootstrapper<String> =
        object : Bootstrapper<String> {
            override fun bootstrap(actionConsumer: (String) -> Unit) {
                bootstrap.invoke(actionConsumer)
            }

            override fun dispose() {
                dispose.invoke()
            }
        }

    private fun executor(
        init: (
            stateSupplier: () -> String,
            resultConsumer: (String) -> Unit,
            labelConsumer: (String) -> Unit
        ) -> Unit = { _, _, _ -> },
        handleIntent: (String) -> Unit = {},
        handleAction: (String) -> Unit = {},
        dispose: () -> Unit = {}
    ): Executor<String, String, String, String, String> =
        object : Executor<String, String, String, String, String> {
            override fun init(stateSupplier: () -> String, resultConsumer: (String) -> Unit, labelConsumer: (String) -> Unit) {
                init.invoke(stateSupplier, resultConsumer, labelConsumer)
            }

            override fun handleIntent(intent: String) {
                handleIntent.invoke(intent)
            }

            override fun handleAction(action: String) {
                handleAction.invoke(action)
            }

            override fun dispose() {
                dispose.invoke()
            }
        }

    private fun <T> T.factory(): () -> T = { this }

    private fun reducer(reduce: String.(String) -> String = { it }): Reducer<String, String> =
        object : Reducer<String, String> {
            override fun String.reduce(result: String): String = reduce(result)
        }
}
