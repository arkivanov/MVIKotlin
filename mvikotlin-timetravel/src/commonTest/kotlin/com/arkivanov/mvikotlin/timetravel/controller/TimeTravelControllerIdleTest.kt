package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
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
    fun processes_result_WHEN_result_emitted_in_idle_state() {
        env.produceResultEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.RESULT, "result1")
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
    fun restores_events() {
        val events =
            listOf(
                env.createIntentEventForStore1(),
                env.createStateEventForStore1(value = "state_1_2", state = "state_1_1"),
                env.createIntentEventForStore2(),
                env.createStateEventForStore2(value = "state_2_2", state = "state_2_1"),
                env.createStateEventForStore1(value = "state_1_3", state = "state_1_2"),
                env.createStateEventForStore2(value = "state_2_3", state = "state_2_2")
            )

        env.controller.restoreEvents(events)

        assertEquals(TimeTravelState.Mode.STOPPED, env.state.mode)
        assertEquals(events, env.events)
    }

    @Test
    fun switched_to_last_state_for_all_stores_WHEN_restore_events() {
        env.controller.restoreEvents(
            listOf(
                env.createIntentEventForStore1(),
                env.createStateEventForStore1(value = "state_1_2", state = "state_1_1"),
                env.createIntentEventForStore2(),
                env.createStateEventForStore2(value = "state_2_2", state = "state_2_1"),
                env.createStateEventForStore1(value = "state_1_3", state = "state_1_2"),
                env.createStateEventForStore2(value = "state_2_3", state = "state_2_2")
            )
        )

        env.store1.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_1_3")
        env.store2.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_2_3")
    }
}
