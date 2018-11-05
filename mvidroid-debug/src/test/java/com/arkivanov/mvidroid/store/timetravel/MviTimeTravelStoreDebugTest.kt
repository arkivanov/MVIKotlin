package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class MviTimeTravelStoreDebugTest {

    private val env = MviTimeTravelStoreTestingEnvironment()
    private val eventProcessor get() = env.store.eventProcessor

    @Test
    fun `valid intent event emitted WHEN intent sent`() {
        env.store.accept("intent")
        env.assertEvents(env.createIntentEvent())
    }

    @Test
    fun `valid action event emitted WHEN precess intent`() {
        whenever(env.intentToAction("intent")).thenReturn("action")
        eventProcessor.process(MviEventType.INTENT, "intent")
        env.assertEvents(env.createActionEvent())
    }

    @Test
    fun `executor executed with valid action WHEN process action`() {
        eventProcessor.process(MviEventType.ACTION, "action")
        verify(env.executorHolder.executor).execute("action")
    }

    @Test
    fun `valid result event emitted WHEN executor dispatched result`() {
        env.executorHolder.resultConsumer("result")
        env.assertEvents(env.createResultEvent())
    }

    @Test
    fun `valid state events emitted WHEN process result`() {
        with(env.reducer) {
            whenever("state".reduce("result1")).thenReturn("state2")
            whenever("state2".reduce("result2")).thenReturn("state3")
        }
        eventProcessor.process(MviEventType.RESULT, "result1")
        eventProcessor.process(MviEventType.RESULT, "result2")
        env.assertEvents(
            env.createStateEvent(value = "state2", state = "state"),
            env.createStateEvent(value = "state3", state = "state2")
        )
    }

    @Test
    fun `executor reads new state WHEN process result`() {
        with(env.reducer) {
            whenever("state".reduce("result1")).thenReturn("state2")
            whenever("state2".reduce("result2")).thenReturn("state3")
        }
        eventProcessor.process(MviEventType.RESULT, "result1")
        eventProcessor.process(MviEventType.RESULT, "result2")
        assertEquals("state3", env.executorHolder.stateSupplier())
    }

    @Test
    fun `store state not emitted WHEN process result`() {
        val observer = TestObserver<String>()
        env.store.states.subscribe(observer)
        observer.values().clear()
        eventProcessor.process(MviEventType.RESULT, "result")
        assertEquals(0, observer.valueCount())
    }

    @Test
    fun `store state not changed WHEN process result`() {
        eventProcessor.process(MviEventType.RESULT, "result")
        assertEquals("state", env.store.state)
    }

    @Test
    fun `valid state emitted WHEN process state`() {
        val observer = TestObserver<String>()
        env.store.states.subscribe(observer)
        observer.values().clear()
        eventProcessor.process(MviEventType.STATE, "new_state")
        assertEquals(listOf("new_state"), observer.values())
    }

    @Test
    fun `valid state WHEN process state`() {
        eventProcessor.process(MviEventType.STATE, "new_state")
        assertEquals("new_state", env.store.state)
    }

    @Test
    fun `valid label event emitted WHEN executor published label`() {
        env.executorHolder.labelConsumer("label")
        env.assertEvents(env.createLabelEvent())
    }

    @Test
    fun `label not emitted WHEN executor published label`() {
        val observer = TestObserver<String>()
        env.store.labels.subscribe(observer)
        env.executorHolder.labelConsumer("label")
        assertEquals(0, observer.valueCount())
    }

    @Test
    fun `label emitted WHEN process label`() {
        val observer = TestObserver<String>()
        env.store.labels.subscribe(observer)
        eventProcessor.process(MviEventType.LABEL, "label")
        assertEquals(listOf("label"), observer.values())
    }

    @Test
    fun `restored actual state WHEN process result AND precess state`() {
        eventProcessor.process(MviEventType.RESULT, "result")
        eventProcessor.process(MviEventType.STATE, "state")
        env.store.restoreState()
        assertEquals("new_state", env.store.state)
    }
}
