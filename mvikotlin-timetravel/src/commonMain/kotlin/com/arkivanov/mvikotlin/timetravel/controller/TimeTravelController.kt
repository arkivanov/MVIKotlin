package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.export.TimeTravelExport

/**
 * Provides methods to control time travel feature.
 *
 * Time travel is a very powerful debug tool. With time travel you can:
 * - Record all events from all active [Store]s
 * - View and explore recorded events
 * - Step throughout recorded events
 * - Fire any event again for debugging
 * - Export and import events
 */
interface TimeTravelController {

    val state: TimeTravelState

    /**
     * Subscribes the provided [Observer] for time travel states, see [TimeTravelState] for more information
     */
    fun states(observer: Observer<TimeTravelState>): Disposable

    /**
     * Starts event recording
     */
    @MainThread
    fun startRecording()

    /**
     * Stops event recording and switches to [TimeTravelState.Mode.STOPPED] mode if at least one event was recorded or
     * to [TimeTravelState.Mode.IDLE] mode if no events were recorded
     */
    @MainThread
    fun stopRecording()

    /**
     * Moves to the beginning of the events (right before the first event, [TimeTravelState.selectedEventIndex] will be -1)
     */
    @MainThread
    fun moveToStart()

    /**
     * Steps to the previous event of type [StoreEventType.STATE],
     * or to the beginning of the events (right before first event, [TimeTravelState.selectedEventIndex] will be -1)
     */
    @MainThread
    fun stepBackward()

    /**
     * Steps to the next event of type [StoreEventType.STATE]
     */
    @MainThread
    fun stepForward()

    /**
     * Moves to the end of the events
     */
    @MainThread
    fun moveToEnd()

    /**
     * Cancels current time travel session and switches to [TimeTravelState.Mode.IDLE] mode
     */
    @MainThread
    fun cancel()

    /**
     * Fires a [TimeTravelEvent] allowing its debugging.
     * Please note that events of type [StoreEventType.STATE] can not be debugged.
     * - If event type is [StoreEventType.INTENT] or [StoreEventType.ACTION], executes the [Executor] of the appropriate Store.
     * A new temporary instance of the [Executor] will be created, its `State` will be same as when original event was recorded,
     * any dispatched `Messages` will be redirected to the [Reducer] and `State` of this temporary [Executor] will be updated,
     * any dispatched `Labels` will be dropped.
     * Original [Executor] will not be executed and `State` of the [Store] will not be changed.
     * - If event type is [StoreEventType.MESSAGE], executes the [Reducer] of the appropriate [Store]. Resulting `State` will be dropped.
     * - If event type is [StoreEventType.STATE], throws an exception as events of type [StoreEventType.STATE] can not be debugged
     * - If event type is [StoreEventType.LABEL], emits the `Label` from the appropriate [Store]
     *
     * @param eventId id of the [TimeTravelEvent] to be debugged
     */
    @MainThread
    fun debugEvent(eventId: Long)

    /**
     * Exports all recorded events and [Store] states into a serializable format
     */
    @MainThread
    fun export(): TimeTravelExport

    /**
     * Sets current mode to [TimeTravelState.Mode.STOPPED] and applies the exported data
     *
     * @param export a previously exported data to by applied
     */
    @MainThread
    fun import(export: TimeTravelExport)
}
