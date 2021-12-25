package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.store.TestTimeTravelStore
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore.Event

internal class TimeTravelControllerTestingEnvironment {

    val store1 = TestTimeTravelStore()
    val store2 = TestTimeTravelStore()

    val controller = TimeTravelControllerImpl()
    val state: TimeTravelState get() = controller.state
    val events: List<TimeTravelEvent> get() = controller.state.events

    init {
        controller.attachStore(store1, "store1")
        controller.attachStore(store2, "store2")
    }

    fun createIntentEvent(value: String = "intent1", state: String = "state1"): Event =
        Event(StoreEventType.INTENT, value, state)

    fun createActionEvent(value: String = "action1", state: String = "state1"): Event =
        Event(StoreEventType.ACTION, value, state)

    fun createMessageEvent(value: String = "message1", state: String = "state1"): Event =
        Event(StoreEventType.MESSAGE, value, state)

    fun createStateEvent(value: String = "state1", state: String = "state1"): Event =
        Event(StoreEventType.STATE, value, state)

    fun createLabelEvent(value: String = "label1", state: String = "state1"): Event =
        Event(StoreEventType.LABEL, value, state)

    fun produceIntentEventForStore1(value: String = "intent1", state: String = "state1"): Event =
        createIntentEvent(value, state).also(store1::sendEvent)

    fun produceActionEventForStore1(value: String = "action1", state: String = "state1"): Event =
        createActionEvent(value, state).also(store1::sendEvent)

    fun produceMessageEventForStore1(value: String = "message1", state: String = "state1"): Event =
        createMessageEvent(value, state).also(store1::sendEvent)

    fun produceStateEventForStore1(value: String = "state1", state: String = "state1"): Event =
        createStateEvent(value, state).also(store1::sendEvent)

    fun produceLabelEventForStore1(value: String = "label1", state: String = "state1"): Event =
        createLabelEvent(value, state).also(store1::sendEvent)

    fun produceIntentEventForStore2(value: String = "intent2", state: String = "state2"): Event =
        createIntentEvent(value, state).also(store2::sendEvent)

    fun produceActionEventForStore2(value: String = "action2", state: String = "state2"): Event =
        createActionEvent(value, state).also(store2::sendEvent)

    fun produceMessageEventForStore2(value: String = "message2", state: String = "state2"): Event =
        createMessageEvent(value, state).also(store2::sendEvent)

    fun produceStateEventForStore2(value: String = "state2", state: String = "state2"): Event =
        createStateEvent(value, state).also(store2::sendEvent)

    fun produceLabelEventForStore2(value: String = "label2", state: String = "state2"): Event =
        createLabelEvent(value, state).also(store2::sendEvent)
}
