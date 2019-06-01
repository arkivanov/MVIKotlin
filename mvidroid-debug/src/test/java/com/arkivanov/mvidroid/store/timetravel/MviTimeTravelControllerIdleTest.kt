package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class MviTimeTravelControllerIdleTest {

    private val env = MviTimeTravelControllerTestingEnvironment()

    @After
    fun after() {
        env.release()
    }

    @Test
    fun `initial state is idle`() {
        assertEquals(MviTimeTravelState.IDLE, env.state)
    }

    @Test
    fun `processes intent WHEN intent emitted in idle state`() {
        env.produceIntentEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.INTENT, "intent1")
    }

    @Test
    fun `processes action WHEN action emitted in idle state`() {
        env.produceActionEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.ACTION, "action1")
    }

    @Test
    fun `processes result WHEN result emitted in idle state`() {
        env.produceResultEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.RESULT, "result1")
    }

    @Test
    fun `processes state WHEN state emitted in idle state`() {
        env.produceStateEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.STATE, "state1")
    }

    @Test
    fun `processes label WHEN label emitted in idle state`() {
        env.produceLabelEventForStore1()
        verify(env.store1EventProcessor).process(MviEventType.LABEL, "label1")
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

        env.controller.restoreEvents(MviTimeTravelEvents(items = events, index = 1))

        assertEquals(MviTimeTravelState.STOPPED, env.state)
        assertEquals(MviTimeTravelEvents(items = events, index = 5), env.events)
    }

    @Test
    fun `switched to last state for all stores WHEN restore events`() {
        env.controller.restoreEvents(
            MviTimeTravelEvents(
                items = listOf(
                    env.createIntentEventForStore1(),
                    env.createStateEventForStore1(value = "state_1_2", state = "state_1_1"),
                    env.createIntentEventForStore2(),
                    env.createStateEventForStore2(value = "state_2_2", state = "state_2_1"),
                    env.createStateEventForStore1(value = "state_1_3", state = "state_1_2"),
                    env.createStateEventForStore2(value = "state_2_3", state = "state_2_2")
                ),
                index = 1
            )
        )

        verify(env.store1EventProcessor).process(MviEventType.STATE, "state_1_3")
        verifyNoMoreInteractions(env.store1EventProcessor)
        verify(env.store2EventProcessor).process(MviEventType.STATE, "state_2_3")
        verifyNoMoreInteractions(env.store2EventProcessor)
    }
}
