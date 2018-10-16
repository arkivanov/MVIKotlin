package com.arkivanov.mvidroid.store.factory.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import com.arkivanov.mvidroid.store.interceptor.timetravel.MviTimeTravelState
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class MviTimeTravelStoreFactoryIdleTest {

    private val env = MviTimeTravelStoreFactoryTestingEnvironment()

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
}
