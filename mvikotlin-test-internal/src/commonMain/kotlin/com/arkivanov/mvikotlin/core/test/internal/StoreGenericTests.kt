package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.isFrozen
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
    fun delivers_messages_from_executor_to_reducer()

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
    fun states_subscriber_not_frozen_WHEN_store_frozen_and_subscribed()

    @Test
    fun labels_subscriber_not_frozen_WHEN_store_frozen_and_subscribed()

    @Test
    fun executor_not_called_WHEN_recursive_intent_on_label()

    @Test
    fun executor_called_WHEN_recursive_intent_on_label_and_first_intent_processed()
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
            var events by atomic(emptyList<String>())

            store(bootstrapper = TestBootstrapper(init = { events = events + "init" }, invoke = { events = events + "invoke" }))

            assertEquals(listOf("init", "invoke"), events)
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
            var events by atomic(emptyList<String>())

            store(
                bootstrapper = TestBootstrapper(init = { events = events + "bootstrapper" }),
                executorFactory = { TestExecutor(init = { events = events + "executor" }) }
            )

            assertEquals(listOf("executor", "bootstrapper"), events)
        }

        override fun delivers_actions_from_bootstrapper_to_executor_after_bootstrap() {
            var actions by atomic(emptyList<String>())
            val bootstrapper = TestBootstrapper()

            store(
                bootstrapper = bootstrapper,
                executorFactory = { TestExecutor(executeAction = { actions = actions + it }) }
            )

            bootstrapper.dispatch("action1")
            bootstrapper.dispatch("action2")

            assertEquals(listOf("action1", "action2"), actions)
        }

        override fun delivers_actions_from_bootstrapper_to_executor_during_bootstrap() {
            var actions by atomic(emptyList<String>())

            store(
                bootstrapper = TestBootstrapper {
                    dispatch("action1")
                    dispatch("action2")
                },
                executorFactory = { TestExecutor(executeAction = { actions = actions + it }) }
            )

            assertEquals(listOf("action1", "action2"), actions)
        }

        override fun does_not_deliver_actions_from_bootstrapper_to_executor_WHEN_disposed_and_bootstrapper_produced_actions() {
            var actions by atomic(emptyList<String>())
            val bootstrapper = TestBootstrapper()

            val store =
                store(
                    bootstrapper = bootstrapper,
                    executorFactory = { TestExecutor(executeAction = { actions = actions + it }) }
                )

            store.dispose()
            bootstrapper.dispatch("action1")
            bootstrapper.dispatch("action2")

            assertEquals(emptyList(), actions)
        }

        override fun produces_labels_from_executor() {
            var labels by atomic(emptyList<String>())
            val executor = TestExecutor()

            val store = store(executorFactory = { executor })
            store.labels(observer(onNext = { labels = labels + it }))

            executor.publish("label1")
            executor.publish("label2")

            assertEquals(listOf("label1", "label2"), labels)
        }

        override fun does_not_produce_labels_from_executor_to_unsubscribed_observer() {
            var labels by atomic(emptyList<String>())
            val executor = TestExecutor()

            val store = store(executorFactory = { executor })
            store.labels(observer(onNext = { labels = labels + it })).dispose()

            executor.publish("label1")
            executor.publish("label2")

            assertEquals(emptyList(), labels)
        }

        override fun delivers_intents_to_executor() {
            var intents by atomic(emptyList<String>())
            val store = store(executorFactory = { TestExecutor(executeIntent = { intents = intents + it }) })

            store.accept("intent1")
            store.accept("intent2")

            assertEquals(listOf("intent1", "intent2"), intents)
        }

        override fun does_not_deliver_intents_to_executor_WHEN_disposed_and_new_intents() {
            var intents by atomic(emptyList<String>())
            val store = store(executorFactory = { TestExecutor(executeIntent = { intents = intents + it }) })

            store.dispose()
            store.accept("intent1")
            store.accept("intent2")

            assertEquals(emptyList(), intents)
        }

        override fun executor_can_read_initial_state() {
            val executor = TestExecutor()
            store(initialState = "initial", executorFactory = { executor })

            assertEquals("initial", executor.state)
        }

        override fun executor_can_read_new_state_WHEN_state_changed() {
            val executor = TestExecutor()
            store(executorFactory = { executor }, reducer = reducer { it })

            executor.dispatch("message")

            assertEquals("message", executor.state)
        }

        override fun delivers_messages_from_executor_to_reducer() {
            var messages by atomic(emptyList<String>())
            val executor = TestExecutor()
            store(
                executorFactory = { executor },
                reducer = reducer {
                    messages = messages + it
                    this
                }
            )

            executor.dispatch("message1")
            executor.dispatch("message2")

            assertEquals(listOf("message1", "message2"), messages)
        }

        override fun state_val_returns_new_state_WHEN_new_state_returned_from_reducer() {
            val executor = TestExecutor()
            val store =
                store(
                    executorFactory = { executor },
                    reducer = reducer { it }
                )

            executor.dispatch("message")

            assertEquals("message", store.state)
        }

        override fun executor_can_read_new_state_WHEN_new_state_returned_from_reducer() {
            val executor = TestExecutor()
            store(
                executorFactory = { executor },
                reducer = reducer { it }
            )

            executor.dispatch("message")

            assertEquals("message", executor.state)
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
            var isCompleted1 by atomic(false)
            var isCompleted2 by atomic(false)
            val store = store()
            store.states(observer(onComplete = { isCompleted1 = true }))
            store.states(observer(onComplete = { isCompleted2 = true }))

            store.dispose()

            assertTrue(isCompleted1)
            assertTrue(isCompleted2)
        }

        override fun labels_observers_completed_WHEN_store_disposed() {
            var isCompleted1 by atomic(false)
            var isCompleted2 by atomic(false)
            val store = store()
            store.labels(observer(onComplete = { isCompleted1 = true }))
            store.labels(observer(onComplete = { isCompleted2 = true }))

            store.dispose()

            assertTrue(isCompleted1)
            assertTrue(isCompleted2)
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

        override fun states_subscriber_not_frozen_WHEN_store_frozen_and_subscribed() {
            val store = store()

            val list = ArrayList<String>()
            store.states(observer { list += it })

            assertFalse(list.isFrozen)
        }

        override fun labels_subscriber_not_frozen_WHEN_store_frozen_and_subscribed() {
            val store = store()

            val list = ArrayList<String>()
            store.labels(observer { list += it })

            assertFalse(list.isFrozen)
        }

        override fun executor_not_called_WHEN_recursive_intent_on_label() {
            var isProcessingIntent by atomic(false)
            var isCalledRecursively by atomic(false)

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

        override fun executor_called_WHEN_recursive_intent_on_label_and_first_intent_processed() {
            var isProcessingIntent by atomic(false)
            var isCalledAfter by atomic(false)

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
            storeFactory(initialState, bootstrapper, executorFactory, reducer)
                .freeze()
                .apply { init() }
    }
