package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("FunctionName", "UnnecessaryAbstractClass")
abstract class StoreGenericTests(
    private val storeFactory: (
        initialState: String,
        bootstrapper: Bootstrapper<String>?,
        executorFactory: () -> Executor<String, String, String, String, String>,
        reducer: Reducer<String, String>
    ) -> Store<String, String, String>
) {

    @Test
    fun does_not_throw_WHEN_second_init() {
        val store = store()

        store.init()
    }

    @Test
    fun state_val_returns_initial_state_WHEN_created() {
        val store = store(initialState = "initial")

        val state = store.state

        assertEquals("initial", state)
    }

    @Test
    fun initializes_bootstrapper_WHEN_created() {
        val bootstrapper = TestBootstrapper()

        store(bootstrapper = bootstrapper)

        assertTrue(bootstrapper.isInitialized)
    }

    @Test
    fun calls_bootstrapper_after_initialization_WHEN_created() {
        val events = ArrayList<String>()

        store(bootstrapper = TestBootstrapper(init = { events += "init" }, invoke = { events += "invoke" }))

        assertEquals(listOf("init", "invoke"), events)
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
        val events = ArrayList<String>()

        store(
            bootstrapper = TestBootstrapper(init = { events += "bootstrapper" }),
            executorFactory = { TestExecutor(init = { events += "executor" }) }
        )

        assertEquals(listOf("executor", "bootstrapper"), events)
    }

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_after_bootstrap() {
        val actions = ArrayList<String>()
        val bootstrapper = TestBootstrapper()

        store(
            bootstrapper = bootstrapper,
            executorFactory = { TestExecutor(executeAction = { actions += it }) }
        )

        bootstrapper.dispatch("action1")
        bootstrapper.dispatch("action2")

        assertEquals(listOf("action1", "action2"), actions)
    }

    @Test
    fun delivers_actions_from_bootstrapper_to_executor_during_bootstrap() {
        val actions = ArrayList<String>()

        store(
            bootstrapper = TestBootstrapper {
                dispatch("action1")
                dispatch("action2")
            },
            executorFactory = { TestExecutor(executeAction = { actions += it }) }
        )

        assertEquals(listOf("action1", "action2"), actions)
    }

    @Test
    fun does_not_deliver_actions_from_bootstrapper_to_executor_WHEN_disposed_and_bootstrapper_produced_actions() {
        val actions = ArrayList<String>()
        val bootstrapper = TestBootstrapper()

        val store =
            store(
                bootstrapper = bootstrapper,
                executorFactory = { TestExecutor(executeAction = { actions += it }) }
            )

        store.dispose()
        bootstrapper.dispatch("action1")
        bootstrapper.dispatch("action2")

        assertEquals(emptyList(), actions)
    }

    @Test
    fun produces_labels_from_executor() {
        val labels = ArrayList<String>()
        val executor = TestExecutor()

        val store = store(executorFactory = { executor })
        store.labels(observer(onNext = { labels += it }))

        executor.publish("label1")
        executor.publish("label2")

        assertEquals(listOf("label1", "label2"), labels)
    }

    @Test
    fun does_not_produce_labels_from_executor_to_unsubscribed_observer() {
        val labels = ArrayList<String>()
        val executor = TestExecutor()

        val store = store(executorFactory = { executor })
        store.labels(observer(onNext = { labels += it })).dispose()

        executor.publish("label1")
        executor.publish("label2")

        assertEquals(emptyList(), labels)
    }

    @Test
    fun delivers_intents_to_executor() {
        val intents = ArrayList<String>()
        val store = store(executorFactory = { TestExecutor(executeIntent = { intents += it }) })

        store.accept("intent1")
        store.accept("intent2")

        assertEquals(listOf("intent1", "intent2"), intents)
    }

    @Test
    fun does_not_deliver_intents_to_executor_WHEN_disposed_and_new_intents() {
        val intents = ArrayList<String>()
        val store = store(executorFactory = { TestExecutor(executeIntent = { intents += it }) })

        store.dispose()
        store.accept("intent1")
        store.accept("intent2")

        assertEquals(emptyList(), intents)
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

        executor.dispatch("message")

        assertEquals("message", executor.state)
    }

    @Test
    fun delivers_messages_from_executor_to_reducer() {
        val messages = ArrayList<String>()
        val executor = TestExecutor()
        store(
            executorFactory = { executor },
            reducer = reducer {
                messages += it
                this
            }
        )

        executor.dispatch("message1")
        executor.dispatch("message2")

        assertEquals(listOf("message1", "message2"), messages)
    }

    @Test
    fun state_val_returns_new_state_WHEN_new_state_returned_from_reducer() {
        val executor = TestExecutor()
        val store =
            store(
                executorFactory = { executor },
                reducer = reducer { it }
            )

        executor.dispatch("message")

        assertEquals("message", store.state)
    }

    @Test
    fun executor_can_read_new_state_WHEN_new_state_returned_from_reducer() {
        val executor = TestExecutor()
        store(
            executorFactory = { executor },
            reducer = reducer { it }
        )

        executor.dispatch("message")

        assertEquals("message", executor.state)
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
        var isCompleted1 = false
        var isCompleted2 = false
        val store = store()
        store.states(observer(onComplete = { isCompleted1 = true }))
        store.states(observer(onComplete = { isCompleted2 = true }))

        store.dispose()

        assertTrue(isCompleted1)
        assertTrue(isCompleted2)
    }

    @Test
    fun labels_observers_completed_WHEN_store_disposed() {
        var isCompleted1 = false
        var isCompleted2 = false
        val store = store()
        store.labels(observer(onComplete = { isCompleted1 = true }))
        store.labels(observer(onComplete = { isCompleted2 = true }))

        store.dispose()

        assertTrue(isCompleted1)
        assertTrue(isCompleted2)
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
    fun executor_not_called_WHEN_recursive_intent_on_label() {
        var isProcessingIntent = false
        var isCalledRecursively = false

        val store =
            store(
                executorFactory = {
                    TestExecutor(
                        executeIntent = {
                            if (it == "intent1") {
                                isProcessingIntent = true
                                publish("label")
                                isProcessingIntent = false
                            } else {
                                isCalledRecursively = isProcessingIntent
                            }
                        }
                    )
                },
                reducer = reducer { it }
            )

        store.labels(observer { store.accept("intent2") })
        store.accept("intent1")

        assertFalse(isCalledRecursively)
    }

    @Test
    fun executor_called_WHEN_recursive_intent_on_label_and_first_intent_processed() {
        var isProcessingIntent = false
        var isCalledAfter = false

        val store =
            store(
                executorFactory = {
                    TestExecutor(
                        executeIntent = {
                            if (it == "intent1") {
                                isProcessingIntent = true
                                publish("label")
                                isProcessingIntent = false
                            } else {
                                isCalledAfter = !isProcessingIntent
                            }
                        }
                    )
                },
                reducer = reducer { it }
            )

        store.labels(observer { store.accept("intent2") })
        store.accept("intent1")

        assertTrue(isCalledAfter)
    }

    private fun store(
        initialState: String = "initial_state",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer()
    ): Store<String, String, String> =
        storeFactory(initialState, bootstrapper, executorFactory, reducer).apply {
            init()
        }
}
