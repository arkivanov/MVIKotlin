package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull

@Suppress("TestFunctionName")
class LabelFlowTest {

    @Test
    fun WHEN_label_emitted_THEN_label_collected() {
        val store = TestStore()
        val flow = store.labels
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val items = ArrayList<Int>()

        scope.launch {
            flow.collect { items += it }
        }

        store.labelObserver?.onNext(1)
        store.labelObserver?.onNext(2)
        store.labelObserver?.onNext(3)

        assertContentEquals(listOf(1, 2, 3), items)
    }

    @Test
    fun WHEN_collection_cancelled_THEN_unsubscribed_from_store() {
        val store = TestStore()
        val flow = store.labels
        val scope = CoroutineScope(Dispatchers.Unconfined)

        scope.launch {
            flow.collect {}
        }

        scope.cancel()

        assertNull(store.labelObserver)
    }

    private class TestStore : Store<Int, Int, Int> {
        override val state: Int = 0
        override val isDisposed: Boolean = false

        var labelObserver: Observer<Int>? = null
            private set

        override fun states(observer: Observer<Int>): Disposable = error("Not required")

        override fun labels(observer: Observer<Int>): Disposable {
            labelObserver = observer

            return Disposable { labelObserver = null }
        }

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
