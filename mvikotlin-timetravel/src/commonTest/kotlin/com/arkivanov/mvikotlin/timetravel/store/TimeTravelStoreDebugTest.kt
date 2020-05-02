package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.arkivanov.mvikotlin.utils.internal.clear
import com.arkivanov.mvikotlin.utils.internal.get
import com.arkivanov.mvikotlin.utils.internal.isEmpty
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.plusAssign
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.freeze
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
        val intent = lateinitAtomicReference<String>()
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

        assertEquals("intent", intent.requireValue)
    }

    @Test
    fun old_executor_not_called_WHEN_debug_intent() {
        val isCalled = AtomicBoolean()
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor(handleAction = { isCalled.value = true })
                } else {
                    TestExecutor()
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "")

        assertFalse(isCalled.value)
    }

    @Test
    fun new_executor_called_WHEN_debug_action() {
        val action = lateinitAtomicReference<String>()
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

        assertEquals("action", action.requireValue)
    }

    @Test
    fun old_executor_not_called_WHEN_debug_action() {
        val isCalled = AtomicBoolean()
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor(handleAction = { isCalled.value = true })
                } else {
                    TestExecutor()
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "")

        assertFalse(isCalled.value)
    }

    @Test
    fun new_executor_reads_original_state_WHEN_debug_intent() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "old_state")

        assertEquals("old_state", executors[1].state)
    }

    @Test
    fun new_executor_reads_original_state_WHEN_debug_action() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "old_state")

        assertEquals("old_state", executors[1].state)
    }

    @Test
    fun new_executor_reads_new_state_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "state")
        executors[1].dispatch("result")

        assertEquals("state_result", executors[1].state)
    }

    @Test
    fun new_executor_reads_new_state_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "state")
        executors[1].dispatch("result")

        assertEquals("state_result", executors[1].state)
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("result")

        assertEquals("initial_state", executors[0].state)
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("result")

        assertEquals("initial_state", executors[0].state)
    }

    @Test
    fun state_not_emitted_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val states = AtomicList<String>()
        store.states(observer(onNext = states::add))
        states.clear()

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("result")

        assertTrue(states.isEmpty)
    }

    @Test
    fun state_not_emitted_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val states = AtomicList<String>()
        store.states(observer(onNext = states::add))
        states.clear()

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("result")

        assertTrue(states.isEmpty)
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
        val labels = AtomicList<String>()
        store.labels(observer(onNext = labels::add))

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].publish("label")

        assertTrue(labels.isEmpty)
    }

    @Test
    fun label_not_emitted_WHEN_debug_action_and_label_published_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val labels = AtomicList<String>()
        store.labels(observer(onNext = labels::add))

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].publish("label")

        assertTrue(labels.isEmpty)
    }

    @Test
    fun reducer_called_with_original_state_and_result_WHEN_debug_result() {
        val state = lateinitAtomicReference<String>()
        val result = lateinitAtomicReference<String>()

        val store =
            store(
                reducer = reducer {
                    state.value = this
                    result.value = it
                    this
                }
            )

        store.debug(type = StoreEventType.RESULT, value = "result", state = "old_state")

        assertEquals("old_state", state.requireValue)
        assertEquals("result", result.requireValue)
    }

    @Test
    fun old_executor_reads_main_state_WHEN_debug_result() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.RESULT, value = "", state = "old_state")

        assertEquals("initial_state", executors[0].state)
    }

    @Test
    fun state_not_emitted_WHEN_debug_result() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val states = AtomicList<String>()
        store.states(observer(onNext = states::add))
        states.clear()

        store.debug(type = StoreEventType.RESULT, value = "", state = "old_state")

        assertTrue(states.isEmpty)
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
        val labels = AtomicList<String>()
        store.labels(observer(onNext = labels::add))

        store.debug(type = StoreEventType.LABEL, value = "label", state = "")

        assertEquals(listOf("label"), labels.value)
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
        private val index = AtomicInt(-1)
        private val executors = AtomicList<TestExecutor>()

        fun next(): TestExecutor =
            factory(index.addAndGet(1))
                .also { executors += it }

        operator fun get(index: Int): TestExecutor = executors[index]
    }
}
