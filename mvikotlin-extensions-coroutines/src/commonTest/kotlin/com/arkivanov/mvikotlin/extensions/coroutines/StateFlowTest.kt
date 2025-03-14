package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TestFunctionName")
class StateFlowTest {

    @Test
    fun WHEN_state_emitted_THEN_state_collected() {
        val store = TestStore()
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val flow = store.stateFlow
        val items = ArrayList<Int>()

        scope.launch {
            flow.collect { items += it }
        }

        store.stateObserver?.onNext(1)
        store.stateObserver?.onNext(2)
        store.stateObserver?.onNext(3)

        assertContentEquals(listOf(0, 1, 2, 3), items)
    }

    @Test
    fun WHEN_collection_cancelled_THEN_unsubscribed_from_store() {
        val store = TestStore()
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val flow = store.stateFlow

        scope.launch {
            flow.collect {}
        }

        scope.cancel()

        assertNull(store.stateObserver)
    }

    private class TestStore : Store<Int, Int, Int> {
        override val state: Int = 0
        override val isDisposed: Boolean = false

        var stateObserver: Observer<Int>? = null
            private set

        override fun states(observer: Observer<Int>): Disposable {
            stateObserver = observer

            return Disposable { stateObserver = null }
        }

        override fun labels(observer: Observer<Int>): Disposable = error("Not required")

        override fun accept(intent: Int) {
            // no-op
        }

        override fun init() {
            // no-op
        }

        override fun dispose() {
            // no-op
        }
    }
}
