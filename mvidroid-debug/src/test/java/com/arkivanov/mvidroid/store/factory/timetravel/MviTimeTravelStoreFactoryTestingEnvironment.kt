package com.arkivanov.mvidroid.store.factory.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import com.arkivanov.mvidroid.store.interceptor.timetravel.MviTimeTravelEvent
import com.arkivanov.mvidroid.store.interceptor.timetravel.MviTimeTravelEvents
import com.arkivanov.mvidroid.store.interceptor.timetravel.MviTimeTravelState
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.subjects.PublishSubject

internal class MviTimeTravelStoreFactoryTestingEnvironment {

    private val store1Events: PublishSubject<MviTimeTravelEvent> = PublishSubject.create<MviTimeTravelEvent>()
    val store1EventProcessor = mock<MviTimeTravelStore<String, String, String, String, String>.EventProcessor>()
    val store1 = mock<MviTimeTravelStore<String, String, String, String, String>> {
        on { events }.thenReturn(store1Events)
        on { eventProcessor }.thenReturn(store1EventProcessor)
    }
    private val store2Events: PublishSubject<MviTimeTravelEvent> = PublishSubject.create<MviTimeTravelEvent>()
    val store2EventProcessor = mock<MviTimeTravelStore<String, String, String, String, String>.EventProcessor>()
    val store2 = mock<MviTimeTravelStore<String, String, String, String, String>> {
        on { events }.thenReturn(store2Events)
        on { eventProcessor }.thenReturn(store2EventProcessor)
    }

    val factory = MviTimeTravelStoreFactory
    val state: MviTimeTravelState get() = factory.states.firstOrError().blockingGet()
    val events: MviTimeTravelEvents get() = factory.events.firstOrError().blockingGet()

    init {
        factory.initStore(store1, "store1")
        factory.initStore(store2, "store2")
    }

    fun release() {
        store1Events.onComplete()
        store2Events.onComplete()
        factory.cancel()
    }

    fun createIntentEventForStore1(value: String = "intent1", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store1", MviEventType.INTENT, value, state)

    fun createActionEventForStore1(value: String = "action1", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store1", MviEventType.ACTION, value, state)

    fun createResultEventForStore1(value: String = "result1", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store1", MviEventType.RESULT, value, state)

    fun createStateEventForStore1(value: String = "state1", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store1", MviEventType.STATE, value, state)

    fun createLabelEventForStore1(value: String = "label1", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store1", MviEventType.LABEL, value, state)

    fun createIntentEventForStore2(value: String = "intent2", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store2", MviEventType.INTENT, value, state)

    fun createActionEventForStore2(value: String = "action2", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store2", MviEventType.ACTION, value, state)

    fun createResultEventForStore2(value: String = "result2", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store2", MviEventType.RESULT, value, state)

    fun createStateEventForStore2(value: String = "state2", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store2", MviEventType.STATE, value, state)

    fun createLabelEventForStore2(value: String = "label2", state: String = "state1"): MviTimeTravelEvent =
        MviTimeTravelEvent("store2", MviEventType.LABEL, value, state)

    fun produceIntentEventForStore1(value: String = "intent1", state: String = "state1") {
        store1Events.onNext(createIntentEventForStore1(value, state))
    }

    fun produceActionEventForStore1(value: String = "action1", state: String = "state1") {
        store1Events.onNext(createActionEventForStore1(value, state))
    }

    fun produceResultEventForStore1(value: String = "result1", state: String = "state1") {
        store1Events.onNext(createResultEventForStore1(value, state))
    }

    fun produceStateEventForStore1(value: String = "state1", state: String = "state1") {
        store1Events.onNext(createStateEventForStore1(value, state))
    }

    fun produceLabelEventForStore1(value: String = "label1", state: String = "state1") {
        store1Events.onNext(createLabelEventForStore1(value, state))
    }

    fun produceIntentEventForStore2(value: String = "intent2", state: String = "state2") {
        store1Events.onNext(createIntentEventForStore2(value, state))
    }

    fun produceActionEventForStore2(value: String = "action2", state: String = "state2") {
        store1Events.onNext(createActionEventForStore2(value, state))
    }

    fun produceResultEventForStore2(value: String = "result2", state: String = "state2") {
        store1Events.onNext(createResultEventForStore2(value, state))
    }

    fun produceStateEventForStore2(value: String = "state2", state: String = "state2") {
        store1Events.onNext(createStateEventForStore2(value, state))
    }

    fun produceLabelEventForStore2(value: String = "label2", state: String = "state2") {
        store1Events.onNext(createLabelEventForStore2(value, state))
    }
}
