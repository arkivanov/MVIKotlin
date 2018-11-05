package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MviTimeTravelControllerRecordingTest {

    private val env = MviTimeTravelControllerTestingEnvironment()

    @Before
    fun before() {
        env.factory.startRecording()
    }

    @After
    fun after() {
        env.release()
    }

    @Test
    fun `state is recording WHEN recording started`() {
        assertEquals(MviTimeTravelState.RECORDING, env.state)
    }

    @Test
    fun `processes intent WHEN intent emitted in recording state`() {
        env.produceIntentEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.INTENT, "intent1")
    }

    @Test
    fun `processes action WHEN action emitted in recording state`() {
        env.produceActionEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.ACTION, "action1")
    }

    @Test
    fun `processes result WHEN result emitted in recording state`() {
        env.produceResultEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.RESULT, "result1")
    }

    @Test
    fun `processes state WHEN state emitted in recording state`() {
        env.produceStateEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.STATE, "state1")
    }

    @Test
    fun `processes label WHEN label emitted in recording state`() {
        env.produceLabelEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.LABEL, "label1")
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
            env.events.items
        )
    }

    @Test
    fun `in idle state WHEN cancelled without events`() {
        env.factory.cancel()
        assertEquals(MviTimeTravelState.IDLE, env.state)
    }

    @Test
    fun `in idle state WHEN cancelled with events`() {
        env.produceIntentEventForStore1()
        env.factory.cancel()
        assertEquals(MviTimeTravelState.IDLE, env.state)
    }

    @Test
    fun `in idle state WHEN stopped without events`() {
        env.factory.stop()
        assertEquals(MviTimeTravelState.IDLE, env.state)
    }
}