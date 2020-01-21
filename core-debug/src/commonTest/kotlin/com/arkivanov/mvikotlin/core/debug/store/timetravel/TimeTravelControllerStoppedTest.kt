package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTravelControllerStoppedTest {

    private lateinit var env: TimeTravelControllerTestingEnvironment

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
        env = TimeTravelControllerTestingEnvironment()
        env.controller.startRecording()
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun `in stopped state WHEN stopped with events`() {
        env.produceIntentEventForStore1()
        env.controller.stopRecording()

        assertEquals(TimeTravelState.Mode.STOPPED, env.state.mode)
    }

    @Test
    fun `in idle state WHEN stopped and cancelled`() {
        env.produceIntentEventForStore1()
        env.controller.stopRecording()
        env.controller.cancel()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun `points to last event WHEN recorded and not stopped`() {
        env.produceIntentEventForStore1()
        env.produceActionEventForStore2()
        env.produceResultEventForStore1()
        env.produceStateEventForStore2()
        env.produceLabelEventForStore1()

        assertEquals(4, env.state.selectedEventIndex)
    }

    @Test
    fun `points to last event WHEN recorded and not stopped and step backward`() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stepBackward()

        assertEquals(1, env.state.selectedEventIndex)
    }

    @Test
    fun `points to previous state WHEN stopped and step backward`() {
        env.produceIntentEventForStore1()
        env.produceActionEventForStore2()
        env.produceResultEventForStore1()
        env.produceStateEventForStore2()
        env.produceLabelEventForStore1()
        env.controller.stopRecording()
        env.controller.stepBackward()

        assertEquals(3, env.state.selectedEventIndex)
    }

    @Test
    fun `points to past previous state WHEN stopped and step backward twice`() {
        env.produceIntentEventForStore1()
        env.produceActionEventForStore1()
        env.produceResultEventForStore1()
        env.produceStateEventForStore1()
        env.produceLabelEventForStore1()
        env.produceIntentEventForStore2()
        env.produceActionEventForStore2()
        env.produceResultEventForStore2()
        env.produceStateEventForStore2()
        env.produceLabelEventForStore2()
        env.controller.stopRecording()
        env.controller.stepBackward()
        env.controller.stepBackward()

        assertEquals(3, env.state.selectedEventIndex)
    }

    @Test
    fun `points to start WHEN stopped and step backward until end`() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.stepBackward()
        env.controller.stepBackward()

        assertEquals(-1, env.state.selectedEventIndex)
    }

    @Test
    fun `points to last state WHEN stopped and step backward and step forward`() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.stepBackward()
        env.controller.stepForward()

        assertEquals(1, env.state.selectedEventIndex)
    }

    @Test
    fun `points to start WHEN stopped and move to start`() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.moveToStart()

        assertEquals(-1, env.state.selectedEventIndex)
    }

    @Test
    fun `points to last event WHEN recorded and move to start and move to end`() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.moveToStart()
        env.controller.moveToEnd()

        assertEquals(2, env.state.selectedEventIndex)
    }

    @Test
    fun `no events processed WHEN moved from end to last state`() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.store1.eventProcessor.reset()
        env.controller.stepBackward()

        env.store1.eventProcessor.assertNoProcessedEvents()
    }

    @Test
    fun `previous state processed WHEN moved from last state to previous event`() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore2(state = "previous_state")
        env.controller.stopRecording()
        env.store2.eventProcessor.reset()
        env.controller.stepBackward()
        env.store2.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "previous_state")
    }

    @Test
    fun `state processed WHEN moved from event past state to state`() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore2(state = "previous_state")
        env.controller.stopRecording()
        env.controller.stepBackward()
        env.store2.eventProcessor.reset()
        env.controller.stepForward()

        env.store2.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state2")
    }

    @Test
    fun `switched to first state for all stores WHEN moved from start`() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceResultEventForStore2()
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.produceResultEventForStore1()
        env.produceStateEventForStore1(value = "state_1_3", state = "state_1_2")
        env.produceResultEventForStore2()
        env.produceStateEventForStore2(value = "state_2_3", state = "state_2_2")
        env.produceResultEventForStore1()
        env.produceResultEventForStore2()
        env.controller.stopRecording()
        env.store1.eventProcessor.reset()
        env.store2.eventProcessor.reset()
        env.controller.moveToStart()

        env.store1.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_1_1")
        env.store2.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_2_1")
    }

    @Test
    fun `switched to last state for all stores WHEN moved from start to last event`() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceResultEventForStore2()
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.produceResultEventForStore1()
        env.produceStateEventForStore1(value = "state_1_3", state = "state_1_2")
        env.produceResultEventForStore2()
        env.produceStateEventForStore2(value = "state_2_3", state = "state_2_2")
        env.produceResultEventForStore1()
        env.produceResultEventForStore2()
        env.controller.stopRecording()
        env.controller.moveToStart()
        env.store1.eventProcessor.reset()
        env.store2.eventProcessor.reset()
        env.controller.moveToEnd()

        env.store1.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_1_3")
        env.store2.eventProcessor.assertSingleProcessedEvent(StoreEventType.STATE, "state_2_3")
    }

    @Test
    fun `second store state processed WHEN first store disposed after stopped`() {
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.controller.stopRecording()
        env.store1.dispose()
        env.store2.eventProcessor.reset()
        env.controller.moveToStart()

        env.store2.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state_2_1")
    }

    @Test
    fun `first store ignored WHEN disposed after stopped`() {
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.controller.stopRecording()
        env.store1.dispose()
        env.store1.eventProcessor.reset()
        env.controller.moveToStart()

        env.store1.eventProcessor.assertNoProcessedEvents()
    }

    @Test
    fun `state restored in all stores WHEN recorded and cancelled`() {
        env.produceResultEventForStore1()
        env.produceResultEventForStore2()
        env.controller.stopRecording()
        env.controller.cancel()

        env.store1.assertStateRestored()
        env.store2.assertStateRestored()
    }
}
