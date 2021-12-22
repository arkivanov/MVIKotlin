package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeTravelStoreDebugTest {

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun new_executor_called_WHEN_debug_intent() {
        val intent = atomic<String>()
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor()
                } else {
                    TestExecutor(handleIntent = { intent.value = it })
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "intent", state = "")

        assertEquals("intent", intent.requireValue())
    }

    @Test
    fun old_executor_not_called_WHEN_debug_intent() {
        var isCalled by atomic(false)
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor(handleAction = { isCalled = true })
                } else {
                    TestExecutor()
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "")

        assertFalse(isCalled)
    }

    @Test
    fun new_executor_called_WHEN_debug_action() {
        val action = atomic<String>()
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor()
                } else {
                    TestExecutor(handleAction = { action.value = it })
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "action", state = "")

        assertEquals("action", action.requireValue())
    }

    @Test
    fun old_executor_not_called_WHEN_debug_action() {
        var isCalled by atomic(false)
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor(handleAction = { isCalled = true })
                } else {
                    TestExecutor()
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "")

        assertFalse(isCalled)
    }

    @Test
    fun new_executor_reads_original_state_WHEN_debug_intent() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "old_state")

        assertEquals("old_state", executors[1].lastStateSupplier())
    }

    @Test
    fun new_executor_reads_original_state_WHEN_debug_action() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "old_state")

        assertEquals("old_state", executors[1].lastStateSupplier())
    }

    @Test
    fun new_executor_reads_new_state_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "state")
        executors[1].dispatch("result")

        assertEquals("state_result", executors[1].lastStateSupplier())
    }

    @Test
    fun new_executor_reads_new_state_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "state")
        executors[1].dispatch("result")

        assertEquals("state_result", executors[1].lastStateSupplier())
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)
        store.process(type = StoreEventType.INTENT, value = "intent")

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("result")

        assertEquals("initial_state", executors[0].lastStateSupplier())
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)
        store.process(type = StoreEventType.INTENT, value = "intent")

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("result")

        assertEquals("initial_state", executors[0].lastStateSupplier())
    }

    @Test
    fun state_not_emitted_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        var states by atomic(emptyList<String>())
        store.states(observer(onNext = { states = states + it }))
        states = emptyList()

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("result")

        assertTrue(states.isEmpty())
    }

    @Test
    fun state_not_emitted_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        var states by atomic(emptyList<String>())
        store.states(observer(onNext = { states = states + it }))
        states = emptyList()

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("result")

        assertTrue(states.isEmpty())
    }

    @Test
    fun state_not_changed_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("result")

        assertEquals("initial_state", store.state)
    }

    @Test
    fun state_not_changed_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("result")

        assertEquals("initial_state", store.state)
    }

    @Test
    fun label_not_emitted_WHEN_debug_intent_and_label_published_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        var labels by atomic(emptyList<String>())
        store.labels(observer(onNext = { labels = labels + it }))

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].publish("label")

        assertTrue(labels.isEmpty())
    }

    @Test
    fun label_not_emitted_WHEN_debug_action_and_label_published_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        var labels by atomic(emptyList<String>())
        store.labels(observer(onNext = { labels = labels + it }))

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].publish("label")

        assertTrue(labels.isEmpty())
    }

    @Test
    fun reducer_called_with_original_state_and_result_WHEN_debug_result() {
        val state = atomic<String>()
        val result = atomic<String>()

        val store =
            store(
                reducer = reducer {
                    state.value = this
                    result.value = it
                    this
                }
            )

        store.debug(type = StoreEventType.RESULT, value = "result", state = "old_state")

        assertEquals("old_state", state.requireValue())
        assertEquals("result", result.requireValue())
    }

    @Test
    fun old_executor_reads_main_state_WHEN_debug_result() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)
        store.process(type = StoreEventType.INTENT, value = "intent")

        store.debug(type = StoreEventType.RESULT, value = "", state = "old_state")

        assertEquals("initial_state", executors[0].lastStateSupplier())
    }

    @Test
    fun state_not_emitted_WHEN_debug_result() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        var states by atomic(emptyList<String>())
        store.states(observer(onNext = { states = states + it }))
        states = emptyList()

        store.debug(type = StoreEventType.RESULT, value = "", state = "old_state")

        assertTrue(states.isEmpty())
    }

    @Test
    fun state_not_changed_WHEN_debug_result() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.RESULT, value = "", state = "old_state")

        assertEquals("initial_state", store.state)
    }

    @Test
    fun exception_WHEN_debug_state() {
        val store = store()

        assertFailsWith<Exception> {
            store.debug(type = StoreEventType.STATE, value = "", state = "")
        }
    }

    @Test
    fun label_emitted_WHEN_debug_label() {
        val store = store()
        var labels by atomic(emptyList<String>())
        store.labels(observer(onNext = { labels = labels + it }))

        store.debug(type = StoreEventType.LABEL, value = "label", state = "")

        assertEquals(listOf("label"), labels)
    }

    private fun store(
        initialState: String = "",
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer()
    ): TimeTravelStore<String, String, String> =
        TimeTravelStoreImpl(
            initialState = initialState,
            bootstrapper = null,
            executorFactory = executorFactory,
            reducer = reducer
        ).apply {
            freeze()
            init()
        }

    private class ExecutorQueue(
        private val factory: (index: Int) -> TestExecutor = { TestExecutor() }
    ) {
        private var index by atomic(0)
        private var executors by atomic(emptyList<TestExecutor>())

        fun next(): TestExecutor =
            factory(index++)
                .also { this.executors += it }

        operator fun get(index: Int): TestExecutor = executors[index]
    }
}
