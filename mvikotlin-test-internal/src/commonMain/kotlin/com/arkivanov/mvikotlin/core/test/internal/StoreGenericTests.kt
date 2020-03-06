package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.plusAssign
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.freeze
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("FunctionName")
interface StoreGenericTests {

    @Test
    fun state_val_returns_initial_state_WHEN_created()

    @Test
    fun initializes_bootstrapper_WHEN_created()

    @Test
    fun calls_bootstrapper_after_initialization_WHEN_created()

    @Test
    fun initializes_executor_WHEN_with_bootstrapper_and_created()

    @Test
    fun initializes_executor_WHEN_without_bootstrapper_and_created()

    @Test
    fun initializes_executor_before_bootstrapper_call_WHEN_with_bootstrapper_and_created()

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_after_bootstrap()

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_during_bootstrap()

    @Test
    fun does_not_deliver_actions_from_bootstrapper_to_executor_WHEN_disposed_and_bootstrapper_produced_actions()

    @Test
    fun produces_labels_from_executor()

    @Test
    fun does_not_produce_labels_from_executor_to_unsubscribed_observer()

    @Test
    fun delivers_intents_to_executor()

    @Test
    fun does_not_deliver_intents_to_executor_WHEN_disposed_and_new_intents()

    @Test
    fun executor_can_read_initial_state()

    @Test
    fun executor_can_read_new_state_WHEN_state_changed()

    @Test
    fun delivers_results_from_executor_to_reducer()

    @Test
    fun state_val_returns_new_state_WHEN_new_state_returned_from_reducer()

    @Test
    fun executor_can_read_new_state_WHEN_new_state_returned_from_reducer()

    @Test
    fun bootstrapper_disposed_WHEN_store_disposed()

    @Test
    fun executor_disposed_WHEN_store_disposed()

    @Test
    fun states_observers_completed_WHEN_store_disposed()

    @Test
    fun labels_observers_completed_WHEN_store_disposed()

    @Test
    fun states_observers_disposables_disposed_WHEN_store_disposed()

    @Test
    fun labels_observers_disposables_disposed_WHEN_store_disposed()

    @Test
    fun store_isDisposed_returns_true_WHEN_store_disposed()

    @Test
    fun executor_can_read_new_state_WHEN_recursive_intent_on_label()

    @Test
    fun executor_can_read_new_state_WHEN_recursive_intent_on_state()
}

@Suppress("FunctionName")
fun StoreGenericTests(
    storeFactory: (
        initialState: String,
        bootstrapper: Bootstrapper<String>?,
        executorFactory: () -> Executor<String, String, String, String, String>,
        reducer: Reducer<String, String>
    ) -> Store<String, String, String>
): StoreGenericTests =
    object : StoreGenericTests {
        override fun state_val_returns_initial_state_WHEN_created() {
            val store = store(initialState = "initial")

            val state = store.state

            assertEquals("initial", state)
        }

        override fun initializes_bootstrapper_WHEN_created() {
            val bootstrapper = TestBootstrapper()

            store(bootstrapper = bootstrapper)

            assertTrue(bootstrapper.isInitialized)
        }

        override fun calls_bootstrapper_after_initialization_WHEN_created() {
            val events = AtomicList<String>()

            store(bootstrapper = TestBootstrapper(init = { events += "init" }, invoke = { events += "invoke" }))

            assertEquals(listOf("init", "invoke"), events.value)
        }

        override fun initializes_executor_WHEN_with_bootstrapper_and_created() {
            val executor = TestExecutor()

            store(bootstrapper = TestBootstrapper(), executorFactory = { executor })

            assertTrue(executor.isInitialized)
        }

        override fun initializes_executor_WHEN_without_bootstrapper_and_created() {
            val executor = TestExecutor()

            store(executorFactory = { executor })

            assertTrue(executor.isInitialized)
        }

        override fun initializes_executor_before_bootstrapper_call_WHEN_with_bootstrapper_and_created() {
            val events = AtomicList<String>()

            store(
                bootstrapper = TestBootstrapper(init = { events += "bootstrapper" }),
                executorFactory = { TestExecutor(init = { events += "executor" }) }
            )

            assertEquals(listOf("executor", "bootstrapper"), events.value)
        }

        override fun delivers_actions_from_bootstrapper_to_executor_after_bootstrap() {
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

        override fun delivers_actions_from_bootstrapper_to_executor_during_bootstrap() {
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

        override fun does_not_deliver_actions_from_bootstrapper_to_executor_WHEN_disposed_and_bootstrapper_produced_actions() {
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

        override fun produces_labels_from_executor() {
            val labels = AtomicList<String>()
            val executor = TestExecutor()

            val store = store(executorFactory = { executor })
            store.labels(observer(onNext = labels::add))

            executor.publish("label1")
            executor.publish("label2")

            assertEquals(listOf("label1", "label2"), labels.value)
        }

        override fun does_not_produce_labels_from_executor_to_unsubscribed_observer() {
            val labels = AtomicList<String>()
            val executor = TestExecutor()

            val store = store(executorFactory = { executor })
            store.labels(observer(onNext = labels::add)).dispose()

            executor.publish("label1")
            executor.publish("label2")

            assertEquals(emptyList(), labels.value)
        }

        override fun delivers_intents_to_executor() {
            val intents = AtomicList<String>()
            val store = store(executorFactory = { TestExecutor(handleIntent = { intents += it }) })

            store.accept("intent1")
            store.accept("intent2")

            assertEquals(listOf("intent1", "intent2"), intents.value)
        }

        override fun does_not_deliver_intents_to_executor_WHEN_disposed_and_new_intents() {
            val intents = AtomicList<String>()
            val store = store(executorFactory = { TestExecutor(handleIntent = { intents += it }) })

            store.dispose()
            store.accept("intent1")
            store.accept("intent2")

            assertEquals(emptyList(), intents.value)
        }

        override fun executor_can_read_initial_state() {
            val executor = TestExecutor()
            store(initialState = "initial", executorFactory = { executor })

            assertEquals("initial", executor.state)
        }

        override fun executor_can_read_new_state_WHEN_state_changed() {
            val executor = TestExecutor()
            store(executorFactory = { executor }, reducer = reducer { it })

            executor.dispatch("result")

            assertEquals("result", executor.state)
        }

        override fun delivers_results_from_executor_to_reducer() {
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

        override fun state_val_returns_new_state_WHEN_new_state_returned_from_reducer() {
            val executor = TestExecutor()
            val store =
                store(
                    executorFactory = { executor },
                    reducer = reducer { it }
                )

            executor.dispatch("result")

            assertEquals("result", store.state)
        }

        override fun executor_can_read_new_state_WHEN_new_state_returned_from_reducer() {
            val executor = TestExecutor()
            store(
                executorFactory = { executor },
                reducer = reducer { it }
            )

            executor.dispatch("result")

            assertEquals("result", executor.state)
        }

        override fun bootstrapper_disposed_WHEN_store_disposed() {
            val bootstrapper = TestBootstrapper()
            val store = store(bootstrapper = bootstrapper)

            store.dispose()

            assertTrue(bootstrapper.isDisposed)
        }

        override fun executor_disposed_WHEN_store_disposed() {
            val executor = TestExecutor()
            val store = store(executorFactory = { executor })

            store.dispose()

            assertTrue(executor.isDisposed)
        }

        override fun states_observers_completed_WHEN_store_disposed() {
            val isCompleted1 = AtomicBoolean()
            val isCompleted2 = AtomicBoolean()
            val store = store()
            store.states(observer(onComplete = { isCompleted1.value = true }))
            store.states(observer(onComplete = { isCompleted2.value = true }))

            store.dispose()

            assertTrue(isCompleted1.value)
            assertTrue(isCompleted2.value)
        }

        override fun labels_observers_completed_WHEN_store_disposed() {
            val isCompleted1 = AtomicBoolean()
            val isCompleted2 = AtomicBoolean()
            val store = store()
            store.labels(observer(onComplete = { isCompleted1.value = true }))
            store.labels(observer(onComplete = { isCompleted2.value = true }))

            store.dispose()

            assertTrue(isCompleted1.value)
            assertTrue(isCompleted2.value)
        }

        override fun states_observers_disposables_disposed_WHEN_store_disposed() {
            val store = store()
            val disposable1 = store.states(observer())
            val disposable2 = store.states(observer())

            store.dispose()

            assertTrue(disposable1.isDisposed)
            assertTrue(disposable2.isDisposed)
        }

        override fun labels_observers_disposables_disposed_WHEN_store_disposed() {
            val store = store()
            val disposable1 = store.labels(observer())
            val disposable2 = store.labels(observer())

            store.dispose()

            assertTrue(disposable1.isDisposed)
            assertTrue(disposable2.isDisposed)
        }

        override fun store_isDisposed_returns_true_WHEN_store_disposed() {
            val store = store()

            store.dispose()

            assertTrue(store.isDisposed)
        }

        override fun executor_can_read_new_state_WHEN_recursive_intent_on_label() {
            val stateRef = lateinitAtomicReference<String>()

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

            store.labels(observer { store.accept("intent2") })
            store.accept("intent1")

            assertEquals("result", stateRef.requireValue)
        }

        override fun executor_can_read_new_state_WHEN_recursive_intent_on_state() {
            val stateRef = lateinitAtomicReference<String>()

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
                observer {
                    if (it == "result") {
                        store.accept("intent2")
                    }
                }
            )
            store.accept("intent1")

            assertEquals("result", stateRef.requireValue)
        }

        private fun store(
            initialState: String = "initial_state",
            bootstrapper: Bootstrapper<String>? = null,
            executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
            reducer: Reducer<String, String> = reducer()
        ): Store<String, String, String> =
            storeFactory(initialState, bootstrapper, executorFactory, reducer).freeze()
    }
