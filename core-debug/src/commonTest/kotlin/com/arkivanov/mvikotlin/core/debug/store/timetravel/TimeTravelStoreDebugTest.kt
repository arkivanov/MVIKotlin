package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.internal.rx.observer
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.arkivanov.mvikotlin.utils.internal.clear
import com.arkivanov.mvikotlin.utils.internal.isEmpty
import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
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
        val intent = lazyAtomicReference<String>()
        val executors = ExecutorQueue(TestExecutor(), TestExecutor(handleIntent = { intent.value = it }))
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(intentEvent())

        assertEquals("intent", intent.requireValue)
    }

    @Test
    fun old_executor_not_called_WHEN_debug_intent() {
        val isCalled = AtomicBoolean()
        val executors = ExecutorQueue(TestExecutor(handleIntent = { isCalled.value = true }), TestExecutor())
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(intentEvent())

        assertFalse(isCalled.value)
    }

    @Test
    fun new_executor_called_WHEN_debug_action() {
        val action = lazyAtomicReference<String>()
        val executors = ExecutorQueue(TestExecutor(), TestExecutor(handleAction = { action.value = it }))
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(actionEvent())

        assertEquals("action", action.requireValue)
    }

    @Test
    fun old_executor_not_called_WHEN_debug_action() {
        val isCalled = AtomicBoolean()
        val executors = ExecutorQueue(TestExecutor(handleAction = { isCalled.value = true }), TestExecutor())
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(actionEvent())

        assertFalse(isCalled.value)
    }

    @Test
    fun new_executor_reads_original_state_WHEN_debug_intent() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(intentEvent(state = "old_state"))

        assertEquals("old_state", newExecutor.state)
    }

    @Test
    fun new_executor_reads_original_state_WHEN_debug_action() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(actionEvent(state = "old_state"))

        assertEquals("old_state", newExecutor.state)
    }

    @Test
    fun new_executor_reads_new_state_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(intentEvent())
        newExecutor.dispatch("result")

        assertEquals("state_result", newExecutor.state)
    }

    @Test
    fun new_executor_reads_new_state_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)

        store.eventDebugger.debug(actionEvent())
        newExecutor.dispatch("result")

        assertEquals("state_result", newExecutor.state)
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val oldExecutor = TestExecutor()
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(oldExecutor, newExecutor)
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.eventDebugger.debug(intentEvent())
        newExecutor.dispatch("result")

        assertEquals("initial_state", oldExecutor.state)
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val oldExecutor = TestExecutor()
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(oldExecutor, newExecutor)
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.eventDebugger.debug(actionEvent())
        newExecutor.dispatch("result")

        assertEquals("initial_state", oldExecutor.state)
    }

    @Test
    fun state_not_emitted_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)
        val states = AtomicList<String>()
        store.states(observer(onNext = states::add))
        states.clear()

        store.eventDebugger.debug(intentEvent())
        newExecutor.dispatch("result")

        assertTrue(states.isEmpty)
    }

    @Test
    fun state_not_emitted_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)
        val states = AtomicList<String>()
        store.states(observer(onNext = states::add))
        states.clear()

        store.eventDebugger.debug(actionEvent())
        newExecutor.dispatch("result")

        assertTrue(states.isEmpty)
    }

    @Test
    fun state_not_changed_WHEN_debug_intent_and_result_dispatched_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.eventDebugger.debug(intentEvent())
        newExecutor.dispatch("result")

        assertEquals("initial_state", store.state)
    }

    @Test
    fun state_not_changed_WHEN_debug_action_and_result_dispatched_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.eventDebugger.debug(actionEvent())
        newExecutor.dispatch("result")

        assertEquals("initial_state", store.state)
    }

    @Test
    fun label_not_emitted_WHEN_debug_intent_and_label_published_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)
        val labels = AtomicList<String>()
        store.labels(observer(onNext = labels::add))

        store.eventDebugger.debug(intentEvent())
        newExecutor.publish("label")

        assertTrue(labels.isEmpty)
    }

    @Test
    fun label_not_emitted_WHEN_debug_action_and_label_published_by_new_executor() {
        val newExecutor = TestExecutor()
        val executors = ExecutorQueue(TestExecutor(), newExecutor)
        val store = store(executorFactory = executors::next)
        val labels = AtomicList<String>()
        store.labels(observer(onNext = labels::add))

        store.eventDebugger.debug(actionEvent())
        newExecutor.publish("label")

        assertTrue(labels.isEmpty)
    }

    @Test
    fun reducer_called_with_original_state_and_result_WHEN_debug_result() {
        val state = lazyAtomicReference<String>()
        val result = lazyAtomicReference<String>()

        val store =
            store(
                reducer = reducer {
                    state.value = this
                    result.value = it
                    this
                }
            )

        store.eventDebugger.debug(resultEvent(state = "old_state"))

        assertEquals("old_state", state.requireValue)
        assertEquals("result", result.requireValue)
    }

    @Test
    fun old_executor_reads_main_state_WHEN_debug_result() {
        val oldExecutor = TestExecutor()
        val executors = ExecutorQueue(oldExecutor, TestExecutor())
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.eventDebugger.debug(resultEvent(state = "old_state"))

        assertEquals("initial_state", oldExecutor.state)
    }

    @Test
    fun state_not_emitted_WHEN_debug_result() {
        val executors = ExecutorQueue(TestExecutor(), TestExecutor())
        val store = store(executorFactory = executors::next)
        val states = AtomicList<String>()
        store.states(observer(onNext = states::add))
        states.clear()

        store.eventDebugger.debug(resultEvent(state = "old_state"))

        assertTrue(states.isEmpty)
    }

    @Test
    fun state_not_changed_WHEN_debug_result() {
        val executors = ExecutorQueue(TestExecutor(), TestExecutor())
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.eventDebugger.debug(resultEvent(state = "old_state"))

        assertEquals("initial_state", store.state)
    }

    @Test
    fun exception_WHEN_debug_state() {
        val store = store()

        assertFailsWith<Exception> {
            store.eventDebugger.debug(stateEvent())
        }
    }

    @Test
    fun label_emitted_WHEN_debug_label() {
        val store = store()
        val labels = AtomicList<String>()
        store.labels(observer(onNext = labels::add))

        store.eventDebugger.debug(labelEvent())

        assertEquals(listOf("label"), labels.value)
    }

    private fun store(
        initialState: String = "",
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer()
    ): TimeTravelStore<String, String, String, String, String> =
        TimeTravelStore(
            name = "store",
            initialState = initialState,
            bootstrapper = null,
            executorFactory = executorFactory,
            reducer = reducer
        ).apply {
            freeze()
            init()
        }

    private fun intentEvent(value: String = "intent", state: String = "state"): TimeTravelEvent =
        TimeTravelEvent("store", StoreEventType.INTENT, value, state)

    private fun actionEvent(value: String = "action", state: String = "state"): TimeTravelEvent =
        TimeTravelEvent("store", StoreEventType.ACTION, value, state)

    private fun resultEvent(value: String = "result", state: String = "state"): TimeTravelEvent =
        TimeTravelEvent("store", StoreEventType.RESULT, value, state)

    private fun stateEvent(value: String = "state", state: String = "state"): TimeTravelEvent =
        TimeTravelEvent("store", StoreEventType.STATE, value, state)

    private fun labelEvent(value: String = "label", state: String = "state"): TimeTravelEvent =
        TimeTravelEvent("store", StoreEventType.LABEL, value, state)

    private class ExecutorQueue(
        private vararg val executors: TestExecutor
    ) {
        private val index = AtomicInt(-1)

        fun next(): TestExecutor = executors[index.addAndGet(1)]
    }
}
