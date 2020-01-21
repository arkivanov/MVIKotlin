package com.arkivanov.mvikotlin.core.debug.store.timetravel

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
    fun does_not_send_debug_events_WHEN_in_STOPPED_state() {
        val event1 = env.createLabelEventForStore1(value = "intent_1_1", state = "state_1_1")
        val event2 = env.createLabelEventForStore2(value = "intent_2_1", state = "state_2_1")
        env.controller.debugEvent(event1)
        env.controller.debugEvent(event2)

        env.store1.eventDebugger.assertNoDebuggedEvents()
        env.store2.eventDebugger.assertNoDebuggedEvents()
    }

    @Test
    fun does_not_send_debug_events_WHEN_in_RECORDING_state() {
        env.controller.startRecording()
        val event1 = env.createLabelEventForStore1(value = "intent_1_1", state = "state_1_1")
        val event2 = env.createLabelEventForStore2(value = "intent_2_1", state = "state_2_1")
        env.controller.debugEvent(event1)
        env.controller.debugEvent(event2)

        env.store1.eventDebugger.assertNoDebuggedEvents()
        env.store2.eventDebugger.assertNoDebuggedEvents()
    }

    @Test
    fun sends_debug_events_WHEN_in_STOPPED_state() {
        env.controller.startRecording()
        env.produceIntentEventForStore1()
        env.produceIntentEventForStore2()
        env.controller.stopRecording()
        val event1 = env.createLabelEventForStore1(value = "intent_1_1", state = "state_1_1")
        val event2 = env.createLabelEventForStore2(value = "intent_2_1", state = "state_2_1")
        env.controller.debugEvent(event1)
        env.controller.debugEvent(event2)

        env.store1.eventDebugger.assertSingleDebuggedEvent(event1)
        env.store2.eventDebugger.assertSingleDebuggedEvent(event2)
    }
}
