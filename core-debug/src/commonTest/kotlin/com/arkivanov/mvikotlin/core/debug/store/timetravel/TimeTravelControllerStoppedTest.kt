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
    fun in_stopped_state_WHEN_stopped_with_events() {
        env.produceIntentEventForStore1()
        env.controller.stopRecording()

        assertEquals(TimeTravelState.Mode.STOPPED, env.state.mode)
    }

    @Test
    fun in_idle_state_WHEN_stopped_and_cancelled() {
        env.produceIntentEventForStore1()
        env.controller.stopRecording()
        env.controller.cancel()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun points_to_last_event_WHEN_recorded_and_not_stopped() {
        env.produceIntentEventForStore1()
        env.produceActionEventForStore2()
        env.produceResultEventForStore1()
        env.produceStateEventForStore2()
        env.produceLabelEventForStore1()

        assertEquals(4, env.state.selectedEventIndex)
    }

    @Test
    fun points_to_last_event_WHEN_recorded_and_not_stopped_and_step_backward() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stepBackward()

        assertEquals(1, env.state.selectedEventIndex)
    }

    @Test
    fun points_to_previous_state_WHEN_stopped_and_step_backward() {
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
    fun points_to_past_previous_state_WHEN_stopped_and_step_backward_twice() {
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
    fun points_to_start_WHEN_stopped_and_step_backward_until_end() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.stepBackward()
        env.controller.stepBackward()

        assertEquals(-1, env.state.selectedEventIndex)
    }

    @Test
    fun points_to_last_state_WHEN_stopped_and_step_backward_and_step_forward() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.stepBackward()
        env.controller.stepForward()

        assertEquals(1, env.state.selectedEventIndex)
    }

    @Test
    fun points_to_start_WHEN_stopped_and_move_to_start() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.moveToStart()

        assertEquals(-1, env.state.selectedEventIndex)
    }

    @Test
    fun points_to_last_event_WHEN_recorded_and_move_to_start_and_move_to_end() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.controller.moveToStart()
        env.controller.moveToEnd()

        assertEquals(2, env.state.selectedEventIndex)
    }

    @Test
    fun no_events_processed_WHEN_moved_from_end_to_last_state() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stopRecording()
        env.store1.eventProcessor.reset()
        env.controller.stepBackward()

        env.store1.eventProcessor.assertNoProcessedEvents()
    }

    @Test
    fun previous_state_processed_WHEN_moved_from_last_state_to_previous_event() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore2(state = "previous_state")
        env.controller.stopRecording()
        env.store2.eventProcessor.reset()
        env.controller.stepBackward()
        env.store2.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "previous_state")
    }

    @Test
    fun state_processed_WHEN_moved_from_event_past_state_to_state() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore2(state = "previous_state")
        env.controller.stopRecording()
        env.controller.stepBackward()
        env.store2.eventProcessor.reset()
        env.controller.stepForward()

        env.store2.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state2")
    }

    @Test
    fun switched_to_first_state_for_all_stores_WHEN_moved_from_start() {
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
    fun switched_to_last_state_for_all_stores_WHEN_moved_from_start_to_last_event() {
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
    fun second_store_state_processed_WHEN_first_store_disposed_after_stopped() {
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.controller.stopRecording()
        env.store1.dispose()
        env.store2.eventProcessor.reset()
        env.controller.moveToStart()

        env.store2.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state_2_1")
    }

    @Test
    fun first_store_ignored_WHEN_disposed_after_stopped() {
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.controller.stopRecording()
        env.store1.dispose()
        env.store1.eventProcessor.reset()
        env.controller.moveToStart()

        env.store1.eventProcessor.assertNoProcessedEvents()
    }

    @Test
    fun state_restored_in_all_stores_WHEN_recorded_and_cancelled() {
        env.produceResultEventForStore1()
        env.produceResultEventForStore2()
        env.controller.stopRecording()
        env.controller.cancel()

        env.store1.assertStateRestored()
        env.store2.assertStateRestored()
    }
}
