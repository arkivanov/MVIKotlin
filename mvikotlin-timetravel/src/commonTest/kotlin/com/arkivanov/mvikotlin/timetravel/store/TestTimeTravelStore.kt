package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.Event
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.clear
import com.arkivanov.mvikotlin.utils.internal.isEmpty
import com.arkivanov.mvikotlin.utils.internal.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestTimeTravelStore : TimeTravelStore<String, String, String> {

    val eventProcessor = TestEventProcessor()
    val eventDebugger = TestEventDebugger()
    private val _events = PublishSubject<Event>()
    private val isStateRestored = AtomicBoolean()

    private val _state = AtomicReference("state")
    override var state: String
        get() = _state.value
        set(value) {
            _state.value = value
        }

    private val _isDisposed = AtomicBoolean()
    override val isDisposed: Boolean get() = _isDisposed.value

    init {
        freeze()
    }

    override fun events(observer: Observer<Event>): Disposable = _events.subscribe(observer)

    fun sendEvent(event: Event) {
        _events.onNext(event)
    }

    override fun init() {
    }

    override fun restoreState() {
        isStateRestored.value = true
    }

    override fun states(observer: Observer<String>): Disposable = TODO()

    override fun labels(observer: Observer<String>): Disposable = TODO()

    override fun accept(intent: String) {
    }

    override fun dispose() {
        _isDisposed.value = true
        _events.onComplete()
    }

    override fun process(type: StoreEventType, value: Any) {
        eventProcessor.process(type, value)
    }

    override fun debug(type: StoreEventType, value: Any, state: Any) {
        eventDebugger.debug(type, value, state)
    }

    fun assertStateRestored() {
        assertTrue(isStateRestored.value)
    }

    class TestEventProcessor {
        private val events = AtomicList<Pair<StoreEventType, Any>>()

        fun process(type: StoreEventType, value: Any) {
            events += type to value
        }

        fun assertProcessedEvent(type: StoreEventType, value: Any) {
            val pair = type to value
            assertEquals(1, events.value.count { it == pair })
        }

        fun assertSingleProcessedEvent(type: StoreEventType, value: Any) {
            assertEquals(listOf(type to value), events.value)
        }

        fun assertNoProcessedEvents() {
            assertTrue(events.isEmpty)
        }

        fun reset() {
            events.clear()
        }
    }

    class TestEventDebugger {
        private val events = AtomicList<Event>()

        fun debug(type: StoreEventType, value: Any, state: Any) {
            events += Event(type, value, state)
        }

        fun assertSingleDebuggedEvent(event: Event) {
            assertEquals(listOf(event), events.value)
        }

        fun assertNoDebuggedEvents() {
            assertTrue(events.isEmpty)
        }
    }
}
