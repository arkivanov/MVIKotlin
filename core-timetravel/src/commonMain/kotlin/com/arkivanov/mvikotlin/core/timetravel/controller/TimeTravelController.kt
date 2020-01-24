package com.arkivanov.mvikotlin.core.timetravel.controller

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.core.timetravel.TimeTravelState

/**
 * Provides methods to control time travel feature.
 * Time travel is a very powerful debug tool. With time travel you can:
 * * record all events of all active Stores
 * * view and explore events
 * * step back and forward over the recorded events
 * * fire any event again to debug
 */
interface TimeTravelController {

    val state: TimeTravelState

    /**
     * Subscribes the provided [Observer] for time travel states, see [TimeTravelState] for more information
     */
    fun states(observer: Observer<TimeTravelState>): Disposable

    /**
     * Sets current state to [TimeTravelState.STOPPED] and replaces any existing events with the provided ones
     */
    @MainThread
    fun restoreEvents(events: List<TimeTravelEvent>)

    /**
     * Starts event recording
     */
    @MainThread
    fun startRecording()

    /**
     * Stops event recording and switches to [TimeTravelState.STOPPED] state if at least one event was recorded or
     * to [TimeTravelState.IDLE] state if no events were recorded
     */
    @MainThread
    fun stopRecording()

    /**
     * Moves to the beginning of the events (right before first event, event index will be -1)
     */
    @MainThread
    fun moveToStart()

    /**
     * Steps to the previous [StoreEventType.STATE] event,
     * or to the beginning of the events (right before first event, event index will be -1)
     */
    @MainThread
    fun stepBackward()

    /**
     * Steps to the next [StoreEventType.STATE] event
     */
    @MainThread
    fun stepForward()

    /**
     * Moves to the end of the events
     */
    @MainThread
    fun moveToEnd()

    /**
     * Cancels time travel session and switches to [TimeTravelState.IDLE] state
     */
    @MainThread
    fun cancel()

    /**
     * Fires the provided event allowing its debugging.
     * Please note that events of type [StoreEventType.STATE]  can not be debugged.
     * * If event type is [StoreEventType.INTENT] or [StoreEventType.ACTION], executes an [Executor] of the appropriate Store.
     * A new temporary instance of [Executor] will be created, its State will be same as when original event was recorded,
     * any dispatched Results will be redirected to the Reducer and State of this temporary [Executor] will be updated,
     * any dispatched Labels will be dropped.
     * Original Executor will not be executed and state of the Store will not be changed.
     * * If event type is [StoreEventType.RESULT], executes a Reducer of the appropriate [Store]. Resulting State will be dropped.
     * * If event type is [StoreEventType.STATE], throws an exception as events of type [StoreEventType.STATE] can not be debugged
     * * If event type is [StoreEventType.LABEL], emits the Label from the appropriate Store
     */
    @MainThread
    fun debugEvent(event: TimeTravelEvent)
}
