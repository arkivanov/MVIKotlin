package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.test.internal.StoreGenericTests
import com.arkivanov.mvikotlin.core.test.internal.TestBootstrapper
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.Event
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class TimeTravelStoreGenericTests : StoreGenericTests(
    storeFactory = { initialState, bootstrapper, executorFactory, reducer ->
        TimeTravelStoreImpl(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        ).apply {
            events(observer { process(it.type, it.value) })
        }
    }
) {

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun WHEN_init_THEN_onInit_called() {
        var isCalled = false

        store(onInit = { isCalled = true })

        assertTrue(isCalled)
    }

    @Test
    fun WHEN_intent_to_store_THEN_event_emitted() {
        val store = store(initialState = "state")
        val events = ArrayList<Event>()
        store.events(observer { events += it })

        store.accept("intent1")
        store.accept("intent2")

        assertContentEquals(
            listOf(
                Event(type = StoreEventType.INTENT, value = "intent1", state = "state"),
                Event(type = StoreEventType.INTENT, value = "intent2", state = "state"),
            ),
            events,
        )
    }

    @Test
    fun WHEN_action_from_bootstrapper_THEN_event_emitted() {
        val bootstrapper = TestBootstrapper()
        val store = store(initialState = "state", bootstrapper = bootstrapper)
        val events = ArrayList<Event>()
        store.events(observer { events += it })

        bootstrapper.dispatch("action1")
        bootstrapper.dispatch("action2")

        assertContentEquals(
            listOf(
                Event(type = StoreEventType.ACTION, value = "action1", state = "state"),
                Event(type = StoreEventType.ACTION, value = "action2", state = "state"),
            ),
            events,
        )
    }

    @Test
    fun WHEN_action_from_executor_THEN_event_emitted() {
        val executor = TestExecutor()
        val store = store(initialState = "state", executorFactory = { executor })
        val events = ArrayList<Event>()
        store.events(observer { events += it })

        executor.forward("action1")
        executor.forward("action2")

        assertContentEquals(
            listOf(
                Event(type = StoreEventType.ACTION, value = "action1", state = "state"),
                Event(type = StoreEventType.ACTION, value = "action2", state = "state"),
            ),
            events,
        )
    }

    @Test
    fun WHEN_message_from_executor_THEN_event_emitted() {
        val executor = TestExecutor()
        val store = store(initialState = "state", executorFactory = { executor })
        val events = ArrayList<Event>()
        store.events(observer { events += it })

        executor.dispatch("message1")
        executor.dispatch("message2")

        assertContentEquals(
            listOf(
                Event(type = StoreEventType.MESSAGE, value = "message1", state = "state"),
                Event(type = StoreEventType.MESSAGE, value = "message2", state = "state"),
            ),
            events,
        )
    }

    @Test
    fun WHEN_label_from_executor_THEN_event_emitted() {
        val executor = TestExecutor()
        val store = store(initialState = "state", executorFactory = { executor })
        val events = ArrayList<Event>()
        store.events(observer { events += it })

        executor.publish("label1")
        executor.publish("label2")

        assertContentEquals(
            listOf(
                Event(type = StoreEventType.LABEL, value = "label1", state = "state"),
                Event(type = StoreEventType.LABEL, value = "label2", state = "state"),
            ),
            events,
        )
    }

    @Test
    fun WHEN_process_message_THEN_state_not_changed() {
        val store = store(initialState = "state", reducer = reducer { "${this}_$it" })

        store.process(type = StoreEventType.MESSAGE, value = "message")

        assertEquals("state", store.state)
    }

    private fun store(
        initialState: String = "initialState",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer(),
        onInit: (TimeTravelStore<String, String, String>) -> Unit = {},
    ): TimeTravelStoreImpl<String, String, String, String, String> =
        TimeTravelStoreImpl(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer,
            onInit = onInit,
        ).apply { init() }
}
