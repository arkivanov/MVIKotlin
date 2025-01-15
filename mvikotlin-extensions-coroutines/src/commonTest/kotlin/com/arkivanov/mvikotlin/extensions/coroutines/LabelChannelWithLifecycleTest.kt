package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class LabelChannelWithLifecycleTest {

    @Test
    fun WHEN_label_emitted_THEN_label_collected() {
        val store = TestStore()
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val channel = store.labelsChannel(LifecycleRegistry())
        val labels = ArrayList<Int>()

        store.labelObserver?.onNext(1)

        scope.launch {
            for (label in channel) {
                labels += label
            }
        }

        store.labelObserver?.onNext(2)
        store.labelObserver?.onNext(3)

        assertContentEquals(listOf(1, 2, 3), labels)
    }

    @Test
    fun WHEN_lifecycle_destroyed_THEN_unsubscribed_from_store() {
        val store = TestStore()
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val lifecycle = LifecycleRegistry(Lifecycle.State.CREATED)
        val channel = store.labelsChannel(lifecycle)

        scope.launch {
            while (true) {
                channel.receive()
            }
        }

        lifecycle.destroy()

        assertNull(store.labelObserver)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun WHEN_lifecycle_destroyed_THEN_channel_cancelled() {
        val store = TestStore()
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val lifecycle = LifecycleRegistry(Lifecycle.State.CREATED)
        val channel = store.labelsChannel(lifecycle)

        scope.launch {
            while (true) {
                channel.receive()
            }
        }

        lifecycle.destroy()

        assertTrue(channel.isClosedForReceive)
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
