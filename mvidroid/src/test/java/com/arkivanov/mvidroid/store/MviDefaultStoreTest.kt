package com.arkivanov.mvidroid.store

import com.arkivanov.kfunction.KConsumer
import com.arkivanov.kfunction.KSupplier
import com.arkivanov.mvidroid.components.MviAction
import com.arkivanov.mvidroid.components.MviBootstrapper
import com.arkivanov.mvidroid.components.MviIntentToAction
import com.arkivanov.mvidroid.components.MviReducer
import com.jakewharton.rxrelay2.ReplayRelay
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class MviDefaultStoreTest {

    private val bootstrapper: Bootstrapper = Bootstrapper()
    private val actionSelector = IntentToAction()
    private val reducer = Reducer()
    private val action = Action()
    private val initialState = State()
    private val store = MviDefaultStore(initialState, bootstrapper, actionSelector, reducer)
    private val states = ReplayRelay.create<State>()
    private val labels = ReplayRelay.create<Any>()

    init {
        store.states.subscribe(states)
        store.labels.subscribe(labels)
    }

    @Test
    fun `bootstrapper invoked WHEN store created`() {
        assertTrue(bootstrapper.isInvoked)
    }

    @Test
    fun `action invoked WHEN bootstrapper dispatched action`() {
        testActionInvoked { bootstrapper.dispatch(action) }
    }

    @Test
    fun `action can read state WHEN bootstrapper dispatched action`() {
        testActionCanReadState { bootstrapper.dispatch(action) }
    }

    @Test
    fun `state updated WHEN bootstrapper dispatched action AND action dispatched result`() {
        testStateUpdatedWhenActionDispatchedResult { bootstrapper.dispatch(action) }
    }

    @Test
    fun `state emitted WHEN bootstrapper dispatched action AND action dispatched result`() {
        testStateEmittedWhenActionDispatchedResult { bootstrapper.dispatch(action) }
    }

    @Test
    fun `label emitted WHEN bootstrapper dispatched action AND action published label`() {
        testLabelEmittedWhenActionPublishedLabel { bootstrapper.dispatch(action) }
    }

    @Test
    fun `disposable disposed WHEN bootstrapper dispatched action AND action returned disposable`() {
        testDisposableDisposedWhenActionReturnedDisposable { bootstrapper.dispatch(action) }
    }

    @Test
    fun `disposable disposed WHEN returned from bootstrapper AND store disposed`() {
        store.dispose()
        verify(bootstrapper.disposable).dispose()
    }

    @Test
    fun `action invoked WHEN intent sent`() {
        testActionInvoked { store("intent") }
    }

    @Test
    fun `action can read state WHEN intent sent`() {
        testActionCanReadState { store("intent") }
    }

    @Test
    fun `state updated WHEN intent sent AND action dispatched result`() {
        testStateUpdatedWhenActionDispatchedResult { store("intent") }
    }

    @Test
    fun `state emitted WHEN intent sent AND action dispatched result`() {
        testStateEmittedWhenActionDispatchedResult { store("intent") }
    }

    @Test
    fun `label emitted WHEN intent sent AND action published label`() {
        testLabelEmittedWhenActionPublishedLabel { store("intent") }
    }

    @Test
    fun `disposable disposed WHEN intent sent AND action returned disposable`() {
        testDisposableDisposedWhenActionReturnedDisposable { store("intent") }
    }

    @Test
    fun `isDisposed=true WHEN store disposed`() {
        store.dispose()
        assertTrue(store.isDisposed)
    }

    @Test
    fun `last action read actual state WHEN two intents for label`() {
        val action1 = Action()
        val action2 = Action()
        val action3 = Action()
        val intentToAction: MviIntentToAction<String, Action> = mock {
            on { select("intent1") }.thenReturn(action1)
            on { select("intent2") }.thenReturn(action2)
            on { select("intent3") }.thenReturn(action3)
        }
        val store = MviDefaultStore(State(), null, intentToAction, Reducer())
        store.labels.subscribe {
            store("intent2")
            store("intent3")
        }
        store("intent1")
        action1.publish("label")
        action2.dispatch("result2")
        assertEquals("result2", action3.getState().data)
    }

    private fun testActionInvoked(block: () -> Unit) {
        block()
        assertTrue(action.isInvoked)
    }

    private fun testActionCanReadState(block: () -> Unit) {
        block()
        assertSame(initialState, action.getState())
    }

    private fun testStateUpdatedWhenActionDispatchedResult(block: () -> Unit) {
        block()
        action.dispatch("result")
        assertEquals("result", store.state.data)
    }

    private fun testStateEmittedWhenActionDispatchedResult(block: () -> Unit) {
        block()
        action.dispatch("result")
        assertArrayEquals(arrayOf(initialState, State("result")), states.values)
    }

    private fun testLabelEmittedWhenActionPublishedLabel(block: () -> Unit) {
        block()
        action.publish("label")
        assertArrayEquals(arrayOf("label"), labels.values)
    }

    private fun testDisposableDisposedWhenActionReturnedDisposable(block: () -> Unit) {
        block()
        store.dispose()
        verify(action.disposable).dispose()
    }

    private data class State(val data: String? = null)

    private class Action : MviAction<State, String, String> {
        var isInvoked: Boolean = false
        lateinit var getState: KSupplier<State>
        lateinit var dispatch: KConsumer<String>
        lateinit var publish: KConsumer<String>
        val disposable: Disposable = mock()

        override fun invoke(getState: KSupplier<State>, dispatch: KConsumer<String>, publish: KConsumer<String>): Disposable? {
            isInvoked = true
            this.getState = getState
            this.dispatch = dispatch
            this.publish = publish
            return disposable
        }
    }

    private class Bootstrapper : MviBootstrapper<Action> {
        var isInvoked: Boolean = false
        lateinit var dispatch: KConsumer<Action>
        val disposable: Disposable = mock()

        override fun bootstrap(dispatch: KConsumer<Action>): Disposable? {
            isInvoked = true
            this.dispatch = dispatch
            return disposable
        }
    }

    private inner class IntentToAction : MviIntentToAction<String, Action> {
        override fun select(intent: String): Action =
            when (intent) {
                "intent" -> action
                else -> throw IllegalStateException("Unsupported intent: $intent")
            }
    }

    private class Reducer : MviReducer<State, String> {
        override fun State.reduce(result: String): State = State(data = result)
    }
}
