package com.arkivanov.mvidroid.store.factory.timetravel

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class MviTimeTravelStoreProcessTest {

    private val env = MviTimeTravelStoreTestingEnvironment()
    private val eventDebugger get() = env.store.eventDebugger

    @Test
    fun `intentToAction called WHEN debug intent`() {
        eventDebugger.debug(env.createIntentEvent())
        verify(env.intentToAction).invoke("intent")
    }

    @Test
    fun `executor not called WHEN debug intent`() {
        eventDebugger.debug(env.createIntentEvent())
        verify(env.executorHolder.executor, never()).execute(any())
    }

    @Test
    fun `new executor called WHEN debug action`() {
        eventDebugger.debug(env.createActionEvent())
        verify(env.newExecutorHolder.executor).execute("action")
    }

    @Test
    fun `old executor not called WHEN debug action`() {
        eventDebugger.debug(env.createActionEvent())
        verify(env.executorHolder.executor, never()).execute(any())
    }

    @Test
    fun `new executor reads original state WHEN debug action`() {
        eventDebugger.debug(env.createActionEvent(state = "old_state"))
        assertEquals("old_state", env.newExecutorHolder.stateSupplier())
    }

    @Test
    fun `new executor reads new state WHEN debug action and result dispatched by new executor`() {
        whenever(env.newExecutorHolder.executor.execute("action")).thenAnswer {
            env.newExecutorHolder.resultConsumer("result")
            null
        }
        eventDebugger.debug(env.createActionEvent())
        assertEquals("new_state", env.newExecutorHolder.stateSupplier())
    }

    @Test
    fun `old executor reads old state WHEN debug action and result dispatched by new executor`() {
        whenever(env.newExecutorHolder.executor.execute("action")).thenAnswer {
            env.newExecutorHolder.resultConsumer("result")
            null
        }
        eventDebugger.debug(env.createActionEvent())
        assertEquals("state", env.executorHolder.stateSupplier())
    }

    @Test
    fun `state not emitted WHEN debug action and result dispatched by new executor`() {
        whenever(env.newExecutorHolder.executor.execute("action")).thenAnswer {
            env.newExecutorHolder.resultConsumer("result")
            null
        }
        val observer = TestObserver<String>()
        env.store.states.subscribe(observer)
        observer.values().clear()
        eventDebugger.debug(env.createActionEvent())
        assertEquals(0, observer.valueCount())
    }

    @Test
    fun `state not changed WHEN debug action and result dispatched by new executor`() {
        whenever(env.newExecutorHolder.executor.execute("action")).thenAnswer {
            env.newExecutorHolder.resultConsumer("result")
            null
        }
        eventDebugger.debug(env.createActionEvent())
        assertEquals("state", env.store.state)
    }

    @Test
    fun `label not emitted WHEN debug action and label published by new executor`() {
        val observer = TestObserver<String>()
        env.store.labels.subscribe(observer)
        eventDebugger.debug(env.createActionEvent())
        env.newExecutorHolder.labelConsumer("label")
        assertEquals(0, observer.valueCount())
    }

    @Test
    fun `reducer called with original state WHEN debug result`() {
        eventDebugger.debug(env.createResultEvent(state = "old_state"))
        with(verify(env.reducer)) {
            "old_state".reduce("result")
        }
    }

    @Test
    fun `old executor reads main state WHEN debug result`() {
        eventDebugger.debug(env.createResultEvent(state = "old_state"))
        assertEquals("state", env.executorHolder.stateSupplier())
    }

    @Test
    fun `state not emitted WHEN debug result`() {
        val observer = TestObserver<String>()
        env.store.states.subscribe(observer)
        observer.values().clear()
        eventDebugger.debug(env.createResultEvent(state = "old_state"))
        assertEquals(0, observer.valueCount())
    }

    @Test
    fun `state not changed WHEN debug result`() {
        eventDebugger.debug(env.createResultEvent(state = "old_state"))
        assertEquals("state", env.store.state)
    }

    @Test(expected = Exception::class)
    fun `exception WHEN debug state`() {
        eventDebugger.debug(env.createStateEvent())
    }

    @Test
    fun `label emitted WHEN debug label`() {
        val observer = TestObserver<String>()
        env.store.labels.subscribe(observer)
        observer.values().clear()
        eventDebugger.debug(env.createLabelEvent())
        assertEquals(listOf("label"), observer.values())
    }
}