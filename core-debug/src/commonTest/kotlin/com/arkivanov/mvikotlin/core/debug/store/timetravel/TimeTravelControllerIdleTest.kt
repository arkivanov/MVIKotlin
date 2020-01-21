package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
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
    fun `initial state is idle`() {
        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun `processes intent WHEN intent emitted in idle state`() {
        env.produceIntentEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.INTENT, "intent1")
    }

    @Test
    fun `processes action WHEN action emitted in idle state`() {
        env.produceActionEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.ACTION, "action1")
    }

    @Test
    fun `processes result WHEN result emitted in idle state`() {
        env.produceResultEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.RESULT, "result1")
    }

    @Test
    fun `processes state WHEN state emitted in idle state`() {
        env.produceStateEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state1")
    }

    @Test
    fun `processes label WHEN label emitted in idle state`() {
        env.produceLabelEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.LABEL, "label1")
    }

    @Test
    fun `restores events`() {
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
    fun `switched to last state for all stores WHEN restore events`() {
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
