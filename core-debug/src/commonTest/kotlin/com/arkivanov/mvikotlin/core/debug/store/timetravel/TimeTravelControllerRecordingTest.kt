package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
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
    fun `state is recording WHEN recording started`() {
        assertEquals(TimeTravelState.Mode.RECORDING, env.state.mode)
    }

    @Test
    fun `processes intent WHEN intent emitted in recording state`() {
        env.produceIntentEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.INTENT, "intent1")
    }

    @Test
    fun `processes action WHEN action emitted in recording state`() {
        env.produceActionEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.ACTION, "action1")
    }

    @Test
    fun `processes result WHEN result emitted in recording state`() {
        env.produceResultEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.RESULT, "result1")
    }

    @Test
    fun `processes state WHEN state emitted in recording state`() {
        env.produceStateEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.STATE, "state1")
    }

    @Test
    fun `processes label WHEN label emitted in recording state`() {
        env.produceLabelEventForStore1()

        env.store1.eventProcessor.assertProcessedEvent(StoreEventType.LABEL, "label1")
    }

    @Test
    fun `events added to list in order WHEN emitted by store`() {
        env.produceIntentEventForStore1()
        env.produceActionEventForStore1()
        env.produceResultEventForStore1()
        env.produceStateEventForStore1()
        env.produceLabelEventForStore1()

        assertEquals(
            listOf(
                env.createIntentEventForStore1(),
                env.createActionEventForStore1(),
                env.createResultEventForStore1(),
                env.createStateEventForStore1(),
                env.createLabelEventForStore1()
            ),
            env.events
        )
    }

    @Test
    fun `in idle state WHEN cancelled without events`() {
        env.controller.cancel()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun `in idle state WHEN cancelled with events`() {
        env.produceIntentEventForStore1()
        env.controller.cancel()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }

    @Test
    fun `in idle state WHEN stopped without events`() {
        env.controller.stopRecording()

        assertEquals(TimeTravelState.Mode.IDLE, env.state.mode)
    }
}
