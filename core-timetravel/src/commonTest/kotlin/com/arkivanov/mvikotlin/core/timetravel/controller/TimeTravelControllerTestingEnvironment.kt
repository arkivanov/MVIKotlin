package com.arkivanov.mvikotlin.core.timetravel.controller

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.timetravel.controller.TimeTravelControllerImpl
import com.arkivanov.mvikotlin.core.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.core.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.core.timetravel.store.TestTimeTravelStore
import com.badoo.reaktive.utils.freeze

internal class TimeTravelControllerTestingEnvironment {

    val store1 = TestTimeTravelStore(name = "store1")
    val store2 = TestTimeTravelStore(name = "store2")

    val controller = TimeTravelControllerImpl().freeze()
    val state: TimeTravelState get() = controller.state
    val events: List<TimeTravelEvent> get() = controller.state.events

    init {
        controller.attachStore(store1)
        controller.attachStore(store2)
    }

    fun createIntentEventForStore1(value: String = "intent1", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store1", StoreEventType.INTENT, value, state)

    fun createActionEventForStore1(value: String = "action1", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store1", StoreEventType.ACTION, value, state)

    fun createResultEventForStore1(value: String = "result1", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store1", StoreEventType.RESULT, value, state)

    fun createStateEventForStore1(value: String = "state1", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store1", StoreEventType.STATE, value, state)

    fun createLabelEventForStore1(value: String = "label1", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store1", StoreEventType.LABEL, value, state)

    fun createIntentEventForStore2(value: String = "intent2", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store2", StoreEventType.INTENT, value, state)

    fun createActionEventForStore2(value: String = "action2", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store2", StoreEventType.ACTION, value, state)

    fun createResultEventForStore2(value: String = "result2", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store2", StoreEventType.RESULT, value, state)

    fun createStateEventForStore2(value: String = "state2", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store2", StoreEventType.STATE, value, state)

    fun createLabelEventForStore2(value: String = "label2", state: String = "state1"): TimeTravelEvent =
        TimeTravelEvent("store2", StoreEventType.LABEL, value, state)

    fun produceIntentEventForStore1(value: String = "intent1", state: String = "state1") {
        store1.sendEvent(createIntentEventForStore1(value, state))
    }

    fun produceActionEventForStore1(value: String = "action1", state: String = "state1") {
        store1.sendEvent(createActionEventForStore1(value, state))
    }

    fun produceResultEventForStore1(value: String = "result1", state: String = "state1") {
        store1.sendEvent(createResultEventForStore1(value, state))
    }

    fun produceStateEventForStore1(value: String = "state1", state: String = "state1") {
        store1.sendEvent(createStateEventForStore1(value, state))
    }

    fun produceLabelEventForStore1(value: String = "label1", state: String = "state1") {
        store1.sendEvent(createLabelEventForStore1(value, state))
    }

    fun produceIntentEventForStore2(value: String = "intent2", state: String = "state2") {
        store2.sendEvent(createIntentEventForStore2(value, state))
    }

    fun produceActionEventForStore2(value: String = "action2", state: String = "state2") {
        store2.sendEvent(createActionEventForStore2(value, state))
    }

    fun produceResultEventForStore2(value: String = "result2", state: String = "state2") {
        store2.sendEvent(createResultEventForStore2(value, state))
    }

    fun produceStateEventForStore2(value: String = "state2", state: String = "state2") {
        store2.sendEvent(createStateEventForStore2(value, state))
    }

    fun produceLabelEventForStore2(value: String = "label2", state: String = "state2") {
        store2.sendEvent(createLabelEventForStore2(value, state))
    }
}
