package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.Event
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestTimeTravelStore : TimeTravelStore<String, String, String> {

    val eventProcessor = TestEventProcessor()
    val eventDebugger = TestEventDebugger()
    private val _events = PublishSubject<Event>()
    private var isStateRestored by atomic(false)
    override var state: String by atomic("state")

    override var isDisposed: Boolean by atomic(false)
        private set

    init {
        freeze()
    }

    override fun events(observer: Observer<Event>): Disposable = _events.subscribe(observer)

    fun sendEvent(event: Event) {
        _events.onNext(event)
    }

    override fun init() {
        // no-op
    }

    override fun restoreState() {
        isStateRestored = true
    }

    override fun states(observer: Observer<String>): Disposable = TODO()

    override fun labels(observer: Observer<String>): Disposable = TODO()

    override fun accept(intent: String) {
        // no-op
    }

    override fun dispose() {
        isDisposed = true
        _events.onComplete()
    }

    override fun process(type: StoreEventType, value: Any) {
        eventProcessor.process(type, value)
    }

    override fun debug(type: StoreEventType, value: Any, state: Any) {
        eventDebugger.debug(type, value, state)
    }

    fun assertStateRestored() {
        assertTrue(isStateRestored)
    }

    class TestEventProcessor {
        private var events by atomic(emptyList<Pair<StoreEventType, Any>>())

        fun process(type: StoreEventType, value: Any) {
            this.events += type to value
        }

        fun assertProcessedEvent(type: StoreEventType, value: Any) {
            val pair = type to value
            assertEquals(1, events.count { it == pair })
        }

        fun assertSingleProcessedEvent(type: StoreEventType, value: Any) {
            assertEquals(listOf(type to value), events)
        }

        fun assertNoProcessedEvents() {
            assertTrue(events.isEmpty())
        }

        fun reset() {
            events = emptyList()
        }
    }

    class TestEventDebugger {
        private var events by atomic(emptyList<Event>())

        fun debug(type: StoreEventType, value: Any, state: Any) {
            this.events += Event(type, value, state)
        }

        fun assertSingleDebuggedEvent(event: Event) {
            assertEquals(listOf(event), events)
        }

        fun assertNoDebuggedEvents() {
            assertTrue(events.isEmpty())
        }
    }
}
