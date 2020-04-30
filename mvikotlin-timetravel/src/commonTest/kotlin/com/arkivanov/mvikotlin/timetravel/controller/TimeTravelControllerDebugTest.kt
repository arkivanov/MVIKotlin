package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TimeTravelControllerDebugTest {

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
    fun does_not_send_debug_events_WHEN_in_IDLE_state() {
        env.produceIntentEventForStore1(value = "intent_1", state = "state_1")
        env.produceLabelEventForStore1(value = "label_2", state = "state_2")
        env.controller.debugEvent(eventId = 1L)
        env.controller.debugEvent(eventId = 2L)

        env.store1.eventDebugger.assertNoDebuggedEvents()
        env.store2.eventDebugger.assertNoDebuggedEvents()
    }

    @Test
    fun does_not_send_debug_events_WHEN_in_RECORDING_state() {
        env.controller.startRecording()
        env.produceIntentEventForStore1(value = "intent_1", state = "state_1")
        env.produceLabelEventForStore1(value = "label_2", state = "state_2")
        env.controller.debugEvent(eventId = 1L)
        env.controller.debugEvent(eventId = 2L)

        env.store1.eventDebugger.assertNoDebuggedEvents()
        env.store2.eventDebugger.assertNoDebuggedEvents()
    }

    @Test
    fun sends_debug_events_WHEN_in_STOPPED_state() {
        env.controller.startRecording()
        val event1 = env.produceIntentEventForStore1(value = "intent_1", state = "state_1")
        val event2 = env.produceLabelEventForStore2(value = "label_2", state = "state_2")
        env.controller.stopRecording()
        env.controller.debugEvent(eventId = 1L)
        env.controller.debugEvent(eventId = 2L)

        env.store1.eventDebugger.assertSingleDebuggedEvent(event1)
        env.store2.eventDebugger.assertSingleDebuggedEvent(event2)
    }
}
