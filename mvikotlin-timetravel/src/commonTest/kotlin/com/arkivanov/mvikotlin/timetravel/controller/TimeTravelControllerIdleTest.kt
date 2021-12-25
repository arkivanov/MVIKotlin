package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.export.TimeTravelExport
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTravelControllerIdleTest {

    private lateinit var env: TimeTravelControllerTestingEnvironment

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
        env = TimeTravelControllerTestingEnvironment()
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun initial_state_is_idle() {
        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun processes_intent_WHEN_intent_emitted_in_idle_state() {
        env.produceIntentEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.INTENT, "intent1")
    }

    @Test
    fun processes_action_WHEN_action_emitted_in_idle_state() {
        env.produceActionEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.ACTION, "action1")
    }

    @Test
    fun processes_message_WHEN_message_emitted_in_idle_state() {
        env.produceMessageEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.MESSAGE, "message1")
    }

    @Test
    fun processes_state_WHEN_state_emitted_in_idle_state() {
        env.produceStateEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state1")
    }

    @Test
    fun processes_label_WHEN_label_emitted_in_idle_state() {
        env.produceLabelEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.LABEL, "label1")
    }

    @Test
    fun mode_STOPPED_WHEN_imported() {
        val export =
            TimeTravelExport(
                recordedEvents = listOf(
                    TimeTravelEvent(id = 1L, storeName = "store1", type = StoreEventType.INTENT, value = "intent_1", state = "state_1"),
                    TimeTravelEvent(id = 2L, storeName = "store1", type = StoreEventType.STATE, value = "state_1", state = "state_2")
                ),
                unusedStoreStates = mapOf("store2" to "store2_state")
            )

        env.controller.import(export)

        assertEquals(TimeTravelState.Mode.STOPPED, env.state.mode)
    }

    @Test
    fun events_restored_WHEN_imported() {
        val export =
            TimeTravelExport(
                recordedEvents = listOf(
                    TimeTravelEvent(id = 1L, storeName = "store1", type = StoreEventType.INTENT, value = "intent_1", state = "state_1"),
                    TimeTravelEvent(id = 2L, storeName = "store1", type = StoreEventType.STATE, value = "state_1", state = "state_2")
                ),
                unusedStoreStates = mapOf("store2" to "store2_state")
            )

        env.controller.import(export)

        assertEquals(export.recordedEvents, env.events)
    }

    @Test
    fun unused_store_states_restored_WHEN_imported() {
        val export =
            TimeTravelExport(
                recordedEvents = listOf(
                    TimeTravelEvent(id = 1L, storeName = "store1", type = StoreEventType.INTENT, value = "intent_1", state = "state_1"),
                    TimeTravelEvent(id = 2L, storeName = "store1", type = StoreEventType.STATE, value = "state_1", state = "state_2")
                ),
                unusedStoreStates = mapOf("store2" to "store2_state")
            )

        env.controller.import(export)

        env.store2.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "store2_state")
    }

    @Test
    fun switched_to_last_state_for_all_stores_WHEN_imported() {
        env.controller.import(
            TimeTravelExport(
                recordedEvents = listOf(
                    TimeTravelEvent(id = 1L, storeName = "store1", type = StoreEventType.INTENT, value = "intent_1_1", state = "state_1_1"),
                    TimeTravelEvent(id = 2L, storeName = "store1", type = StoreEventType.STATE, value = "state_1_2", state = "state_1_1"),
                    TimeTravelEvent(id = 3L, storeName = "store2", type = StoreEventType.INTENT, value = "intent_2_1", state = "state_2_1"),
                    TimeTravelEvent(id = 4L, storeName = "store2", type = StoreEventType.STATE, value = "state_2_2", state = "state_2_1"),
                    TimeTravelEvent(id = 5L, storeName = "store1", type = StoreEventType.STATE, value = "state_1_3", state = "state_1_2"),
                    TimeTravelEvent(id = 6L, storeName = "store2", type = StoreEventType.STATE, value = "state_2_3", state = "state_2_2")
                ),
                unusedStoreStates = emptyMap()
            )
        )

        env.store1.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_1_3")
        env.store2.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_2_3")
    }
}
