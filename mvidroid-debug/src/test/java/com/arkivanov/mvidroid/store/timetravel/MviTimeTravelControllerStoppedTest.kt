package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MviTimeTravelControllerStoppedTest {

    private val env = MviTimeTravelControllerTestingEnvironment()

    @Before
    fun before() {
        env.controller.startRecording()
    }

    @After
    fun after() {
        env.release()
    }

    @Test
    fun `in stopped state WHEN stopped with events`() {
        env.produceIntentEventForStore1()
        env.controller.stop()
        assertEquals(MviTimeTravelState.STOPPED, env.state)
    }

    @Test
    fun `in idle state WHEN stopped and cancelled`() {
        env.produceIntentEventForStore1()
        env.controller.stop()
        env.controller.cancel()
        assertEquals(MviTimeTravelState.IDLE, env.state)
    }

    @Test
    fun `points to last event WHEN recorded and not stopped`() {
        env.produceIntentEventForStore1()
        env.produceActionEventForStore2()
        env.produceResultEventForStore1()
        env.produceStateEventForStore2()
        env.produceLabelEventForStore1()
        assertEquals(4, env.events.index)
    }

    @Test
    fun `points to last event WHEN recorded and not stopped and step backward`() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stepBackward()
        assertEquals(1, env.events.index)
    }

    @Test
    fun `points to previous state WHEN stopped and step backward`() {
        env.produceIntentEventForStore1()
        env.produceActionEventForStore2()
        env.produceResultEventForStore1()
        env.produceStateEventForStore2()
        env.produceLabelEventForStore1()
        env.controller.stop()
        env.controller.stepBackward()
        assertEquals(3, env.events.index)
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
        env.controller.stop()
        env.controller.stepBackward()
        env.controller.stepBackward()
        assertEquals(3, env.events.index)
    }

    @Test
    fun `points to start WHEN stopped and step backward until end`() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stop()
        env.controller.stepBackward()
        env.controller.stepBackward()
        assertEquals(-1, env.events.index)
    }

    @Test
    fun `points to last state WHEN stopped and step backward and step forward`() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stop()
        env.controller.stepBackward()
        env.controller.stepForward()
        assertEquals(1, env.events.index)
    }

    @Test
    fun `points to start WHEN stopped and move to start`() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stop()
        env.controller.moveToStart()
        assertEquals(-1, env.events.index)
    }

    @Test
    fun `points to last event WHEN recorded and move to start and move to end`() {
        env.produceStateEventForStore2()
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stop()
        env.controller.moveToStart()
        env.controller.moveToEnd()
        assertEquals(2, env.events.index)
    }

    @Test
    fun `no events processed WHEN moved from end to last state`() {
        env.produceStateEventForStore1()
        env.produceResultEventForStore1()
        env.controller.stop()
        clearInvocations(env.store1EventProcessor)
        env.controller.stepBackward()
        verify(env.store1EventProcessor, never()).process(any(), any())
    }

    @Test
    fun `previous state processed WHEN moved from last state to previous event`() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore2(state = "previous_state")
        env.controller.stop()
        clearInvocations(env.store2EventProcessor)
        env.controller.stepBackward()
        verify(env.store2EventProcessor).process(MviEventType.STATE, "previous_state")
    }

    @Test
    fun `state processed WHEN moved from event past state to state`() {
        env.produceResultEventForStore1()
        env.produceStateEventForStore2(state = "previous_state")
        env.controller.stop()
        env.controller.stepBackward()
        clearInvocations(env.store2EventProcessor)
        env.controller.stepForward()
        verify(env.store2EventProcessor).process(MviEventType.STATE, "state2")
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
        env.controller.stop()
        clearInvocations(env.store1EventProcessor)
        clearInvocations(env.store2EventProcessor)
        env.controller.moveToStart()
        verify(env.store1EventProcessor).process(MviEventType.STATE, "state_1_1")
        verifyNoMoreInteractions(env.store1EventProcessor)
        verify(env.store2EventProcessor).process(MviEventType.STATE, "state_2_1")
        verifyNoMoreInteractions(env.store2EventProcessor)
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
        env.controller.stop()
        env.controller.moveToStart()
        clearInvocations(env.store1EventProcessor)
        clearInvocations(env.store2EventProcessor)
        env.controller.moveToEnd()
        verify(env.store1EventProcessor).process(MviEventType.STATE, "state_1_3")
        verifyNoMoreInteractions(env.store1EventProcessor)
        verify(env.store2EventProcessor).process(MviEventType.STATE, "state_2_3")
        verifyNoMoreInteractions(env.store2EventProcessor)
    }

    @Test
    fun `second store state processed WHEN first store disposed after stopped`() {
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.controller.stop()
        env.store1Events.onComplete()
        clearInvocations(env.store2EventProcessor)
        env.controller.moveToStart()
        verify(env.store2EventProcessor).process(MviEventType.STATE, "state_2_1")
    }

    @Test
    fun `first store ignored WHEN disposed after stopped`() {
        env.produceStateEventForStore1(value = "state_1_2", state = "state_1_1")
        env.produceStateEventForStore2(value = "state_2_2", state = "state_2_1")
        env.controller.stop()
        env.store1Events.onComplete()
        clearInvocations(env.store1EventProcessor)
        env.controller.moveToStart()
        verify(env.store1EventProcessor, never()).process(any(), any())
    }

    @Test
    fun `state restored in all stores WHEN recorded and cancelled`() {
        env.produceResultEventForStore1()
        env.produceResultEventForStore2()
        env.controller.stop()
        env.controller.cancel()
        verify(env.store1).restoreState()
        verify(env.store2).restoreState()
    }
}