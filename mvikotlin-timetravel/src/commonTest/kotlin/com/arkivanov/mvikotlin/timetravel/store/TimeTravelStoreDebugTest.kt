package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.rx.observer
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
        var intent: String? = null
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor()
                } else {
                    TestExecutor(executeIntent = { intent = it })
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "intent", state = "")

        assertEquals("intent", intent)
    }

    @Test
    fun old_executor_not_called_WHEN_debug_intent() {
        var isCalled = false
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor(executeAction = { isCalled = true })
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
        var action: String? = null
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor()
                } else {
                    TestExecutor(executeAction = { action = it })
                }
            }
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "action", state = "")

        assertEquals("action", action)
    }

    @Test
    fun old_executor_not_called_WHEN_debug_action() {
        var isCalled = false
        val executors =
            ExecutorQueue { index ->
                if (index == 0) {
                    TestExecutor(executeAction = { isCalled = true })
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
    fun new_executor_reads_new_state_WHEN_debug_intent_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "state")
        executors[1].dispatch("message")

        assertEquals("state_message", executors[1].state)
    }

    @Test
    fun new_executor_reads_new_state_WHEN_debug_action_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "state")
        executors[1].dispatch("message")

        assertEquals("state_message", executors[1].state)
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_intent_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("message")

        assertEquals("initial_state", executors[0].state)
    }

    @Test
    fun old_executor_reads_old_state_WHEN_debug_action_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("message")

        assertEquals("initial_state", executors[0].state)
    }

    @Test
    fun state_not_emitted_WHEN_debug_intent_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val states = ArrayList<String>()
        store.states(observer(onNext = { states += it }))
        states.clear()

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("message")

        assertTrue(states.isEmpty())
    }

    @Test
    fun state_not_emitted_WHEN_debug_action_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val states = ArrayList<String>()
        store.states(observer(onNext = { states += it }))
        states.clear()

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("message")

        assertTrue(states.isEmpty())
    }

    @Test
    fun state_not_changed_WHEN_debug_intent_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].dispatch("message")

        assertEquals("initial_state", store.state)
    }

    @Test
    fun state_not_changed_WHEN_debug_action_and_message_dispatched_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].dispatch("message")

        assertEquals("initial_state", store.state)
    }

    @Test
    fun label_not_emitted_WHEN_debug_intent_and_label_published_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val labels = ArrayList<String>()
        store.labels(observer(onNext = { labels += it }))

        store.debug(type = StoreEventType.INTENT, value = "", state = "")
        executors[1].publish("label")

        assertTrue(labels.isEmpty())
    }

    @Test
    fun label_not_emitted_WHEN_debug_action_and_label_published_by_new_executor() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val labels = ArrayList<String>()
        store.labels(observer(onNext = { labels += it }))

        store.debug(type = StoreEventType.ACTION, value = "", state = "")
        executors[1].publish("label")

        assertTrue(labels.isEmpty())
    }

    @Test
    fun reducer_called_with_original_state_and_message_WHEN_debug_message() {
        var state: String? = null
        var message: String? = null

        val store =
            store(
                reducer = reducer {
                    state = this
                    message = it
                    this
                }
            )

        store.debug(type = StoreEventType.MESSAGE, value = "message", state = "old_state")

        assertEquals("old_state", state)
        assertEquals("message", message)
    }

    @Test
    fun old_executor_reads_main_state_WHEN_debug_message() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.MESSAGE, value = "", state = "old_state")

        assertEquals("initial_state", executors[0].state)
    }

    @Test
    fun state_not_emitted_WHEN_debug_message() {
        val executors = ExecutorQueue()
        val store = store(executorFactory = executors::next)
        val states = ArrayList<String>()
        store.states(observer(onNext = { states += it }))
        states.clear()

        store.debug(type = StoreEventType.MESSAGE, value = "", state = "old_state")

        assertTrue(states.isEmpty())
    }

    @Test
    fun state_not_changed_WHEN_debug_message() {
        val executors = ExecutorQueue()
        val store = store(initialState = "initial_state", executorFactory = executors::next)

        store.debug(type = StoreEventType.MESSAGE, value = "", state = "old_state")

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
        val labels = ArrayList<String>()
        store.labels(observer(onNext = { labels += it }))

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
        ).apply { init() }

    private class ExecutorQueue(
        private val factory: (index: Int) -> TestExecutor = { TestExecutor() }
    ) {
        private var index = 0
        private var executors = ArrayList<TestExecutor>()

        fun next(): TestExecutor =
            factory(index++)
                .also { this.executors += it }

        operator fun get(index: Int): TestExecutor = executors[index]
    }
}
