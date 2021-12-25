package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTravelControllerRecordingTest {

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
    fun state_is_recording_WHEN_recording_started() {
        assertEquals(TimeTravelState.Mode.RECORDING, env.state.mode)
    }

    @Test
    fun processes_intent_WHEN_intent_emitted_in_recording_state() {
        env.produceIntentEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.INTENT, "intent1")
    }

    @Test
    fun processes_action_WHEN_action_emitted_in_recording_state() {
        env.produceActionEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.ACTION, "action1")
    }

    @Test
    fun processes_messages_WHEN_message_emitted_in_recording_state() {
        env.produceMessageEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.MESSAGE, "message1")
    }

    @Test
    fun processes_state_WHEN_state_emitted_in_recording_state() {
        env.produceStateEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state1")
    }

    @Test
    fun processes_label_WHEN_label_emitted_in_recording_state() {
        env.produceLabelEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.LABEL, "label1")
    }

    @Test
    fun events_added_to_list_in_order_WHEN_emitted_by_store() {
        val events =
            listOf(
                env.produceIntentEventForStore1(),
                env.produceActionEventForStore1(),
                env.produceMessageEventForStore1(),
                env.produceStateEventForStore1(),
                env.produceLabelEventForStore1()
            )

        assertEquals(
            events.mapIndexed { index, event ->
                TimeTravelEvent(
                    id = index + 1L,
                    storeName = "store1",
                    type = event.type,
                    value = event.value,
                    state = event.state
                )
            },
            env.events
        )
    }

    @Test
    fun in_idle_state_WHEN_cancelled_without_events() {
        env.controller.cancel()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun in_idle_state_WHEN_cancelled_with_events() {
        env.produceIntentEventForStore1()
        env.controller.cancel()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun in_idle_state_WHEN_stopped_without_events() {
        env.controller.stopRecording()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun events_cleared_WHEN_cancelled() {
        env.produceStateEventForStore1()
        env.produceStateEventForStore2()
        env.controller.cancel()

        assertEquals(emptyList(), env.events)
        assertEquals(-1, env.state.selectedEventIndex)
    }
}
