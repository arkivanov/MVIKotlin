package com.arkivanov.mvidroid.store.timetravel

import android.annotation.SuppressLint
import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.MviEventType
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.ArrayList
import java.util.Deque
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList

/**
 * Provides methods to control time travel feature.
 * Time travel is a very powerful debug tool. With time travel you can:
 * * record all events of all active Stores
 * * view and explore events
 * * step back and forward over the recorded events
 * * fire any event again to debug
 *
 * See [MviTimeTravelStoreFactory], [MviTimeTravelView][com.arkivanov.mvidroid.widget.MviTimeTravelView]
 * and [MviTimeTravelDrawer][com.arkivanov.mvidroid.widget.MviTimeTravelDrawer] for more information.
 */
object MviTimeTravelController {

    private val eventsSubject = BehaviorSubject.createDefault(MviTimeTravelEvents())
    private val statesSubject = BehaviorSubject.createDefault<MviTimeTravelState>(MviTimeTravelState.IDLE)
    private val postponedEvents = ArrayList<MviTimeTravelEvent>()
    private val stores = HashMap<String, MviTimeTravelStore<*, *, *, *, *>>()

    /**
     * Returns current time travel state, see [MviTimeTravelState] for more information
     */
    var state: MviTimeTravelState
        get() = statesSubject.value!!
        private set(value) {
            statesSubject.onNext(value)
        }

    /**
     * Returns current time travel events, see [MviTimeTravelEvents] for more information
     */
    var events: MviTimeTravelEvents
        get() = eventsSubject.value!!
        private set(value) {
            eventsSubject.onNext(value)
        }

    /**
     * Observable of time travel state, see [MviTimeTravelState] for more information
     */
    val stateUpdates: Observable<MviTimeTravelState> = statesSubject

    /**
     * Obserable of time travel events, see [MviTimeTravelEvents] for more information
     */
    val eventsUpdates: Observable<MviTimeTravelEvents> = eventsSubject

    @SuppressLint("CheckResult")
    internal fun <State : Any, Intent : Any, Action : Any, Result : Any, Label : Any> attachStore(
        store: MviTimeTravelStore<State, Intent, Action, Result, Label>,
        storeName: String
    ) {
        if (stores.containsKey(storeName)) {
            throw IllegalStateException("Duplicate store: $storeName")
        } else {
            stores[storeName] = store
        }

        store.events.subscribe(::onEvent, {}, { stores.remove(storeName) })
        store.init()
    }

    /**
     * Sets current state to [MviTimeTravelState.STOPPED] and replaces any existing events with the provided ones
     */
    @MainThread
    fun restoreEvents(events: MviTimeTravelEvents) {
        if (events.items.isNotEmpty()) {
            state = MviTimeTravelState.STOPPED
            this.events = events.copy(index = -1)
            moveToEnd()
        }
    }

    /**
     * Starts event recording
     */
    @MainThread
    fun startRecording() {
        if (state === MviTimeTravelState.IDLE) {
            state = MviTimeTravelState.RECORDING
        }
    }

    /**
     * Stops event recording and switches to STOPPED state if at least one event was recorded or
     * to IDLE state if no events were recorded
     */
    @MainThread
    fun stop() {
        if (state === MviTimeTravelState.RECORDING) {
            state = if (events.items.isNotEmpty()) MviTimeTravelState.STOPPED else MviTimeTravelState.IDLE
        }
    }

    /**
     * Moves to the beginning of the events (right before first event, event index will be -1)
     */
    @MainThread
    fun moveToStart() {
        if (state === MviTimeTravelState.STOPPED) {
            move(events, -1)
        }
    }

    /**
     * Steps to the previous STATE event, or to the beginning of the events (right before first event, event index will be -1)
     */
    @MainThread
    fun stepBackward() {
        if (state === MviTimeTravelState.STOPPED) {
            step(events, false)
        }
    }

    /**
     * Steps to the next STATE event
     */
    @MainThread
    fun stepForward() {
        if (state === MviTimeTravelState.STOPPED) {
            step(events, true)
        }
    }

    /**
     * Moves to the end of the events
     */
    @MainThread
    fun moveToEnd() {
        if (state === MviTimeTravelState.STOPPED) {
            move(events, events.items.lastIndex)
        }
    }

    /**
     * Cancels time travel session and switches to IDLE state
     */
    @MainThread
    fun cancel() {
        if (state !== MviTimeTravelState.IDLE) {
            events = MviTimeTravelEvents()
            val oldState = state
            state = MviTimeTravelState.IDLE

            if (oldState !== MviTimeTravelState.RECORDING) {
                stores.values.forEach { it.restoreState() }
                postponedEvents.forEach { process(it) }
            }

            postponedEvents.clear()
        }
    }

    /**
     * Fires the provided event allowing its debugging.
     * Please note that events of type STATE can not be debugged.
     * * If event type is INTENT, executes intentToAction function of the appropriate Store. Resulting Action will be dropped.
     * * If event type is ACTION, executes an Executor of the appropriate Store.
     * A new temporary instance of Executor will be created, its State will be same as when original event was recorded,
     * any dispatched Resutls will be redirected to the Reducer and State of this temporary Executor will be updated,
     * any dispatched Labels will be dropped.
     * Original Executor will not be executed and state of the Store will not be changed.
     * * If event type is RESULT, executes a Reducer of the appropriate Store. Resulting State will be dropped.
     * * If event type is STATE, throws an exception as events of type STATE can not be debugged
     * * If event type is LABEL, emits the Label from the appropriate Store
     */
    @MainThread
    fun debugEvent(event: MviTimeTravelEvent) {
        stores[event.storeName]?.eventDebugger?.debug(event)
    }

    private fun onEvent(event: MviTimeTravelEvent) {
        when {
            state === MviTimeTravelState.RECORDING -> {
                events = events.copy(
                    items = events.items + event,
                    index = events.items.size
                )
                process(event)
            }

            state === MviTimeTravelState.IDLE -> process(event)

            state === MviTimeTravelState.STOPPED -> {
                if (event.type === MviEventType.RESULT) {
                    process(event)
                } else {
                    postponedEvents.add(event)
                }
            }
        }
    }

    private fun step(events: MviTimeTravelEvents, isForward: Boolean) {
        with(events) {
            val progression =
                if (isForward) {
                    index + 1..items.lastIndex
                } else {
                    index - 1 downTo -1
                }

            for (i in progression) {
                val item = items.getOrNull(i)
                if ((item == null) || (item.type === MviEventType.STATE)) {
                    move(events, i)
                    break
                }
            }
        }
    }

    private fun move(events: MviTimeTravelEvents, to: Int, publish: Boolean = true) {
        val from = events.index
        if (from == to) {
            return
        }

        val set = HashSet<String>()
        val deque: Deque<MviTimeTravelEvent> = LinkedList()
        val isForward = to > from
        val progression =
            if (isForward) {
                to downTo from + 1
            } else {
                to + 1..from
            }

        for (i in progression) {
            val event = events.items[i]
            if ((event.type === MviEventType.STATE) && stores.containsKey(event.storeName) && !set.contains(event.storeName)) {
                set.add(event.storeName)
                deque.push(event)
                if (set.size == stores.size) {
                    break
                }
            }
        }
        while (!deque.isEmpty()) {
            deque.pop().also { event ->
                if ((event.type === MviEventType.STATE) && !isForward) {
                    process(event, event.state)
                } else {
                    process(event)
                }
            }
        }

        if (publish) {
            this.events = events.copy(index = to)
        }
    }

    private fun process(event: MviTimeTravelEvent, previousValue: Any? = null) {
        stores[event.storeName]?.eventProcessor?.process(event.type, previousValue ?: event.value)
    }
}
