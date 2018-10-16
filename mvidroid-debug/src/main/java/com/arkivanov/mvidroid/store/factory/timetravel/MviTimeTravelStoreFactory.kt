package com.arkivanov.mvidroid.store.factory.timetravel

import android.annotation.SuppressLint
import android.support.annotation.MainThread
import android.support.annotation.VisibleForTesting
import com.arkivanov.mvidroid.store.MviEventType
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import com.arkivanov.mvidroid.store.interceptor.timetravel.MviTimeTravelEvent
import com.arkivanov.mvidroid.store.interceptor.timetravel.MviTimeTravelEvents
import com.arkivanov.mvidroid.store.interceptor.timetravel.MviTimeTravelState
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

/**
 * An implementation of [MviStoreFactory] that provides time travel.
 * Time travel is a very powerful debug tool.
 * See [MviTimeTravelView][com.arkivanov.mvidroid.widget.MviTimeTravelView]
 * and [MviTimeTravelDrawer][com.arkivanov.mvidroid.widget.MviTimeTravelDrawer] for more information.
 */
object MviTimeTravelStoreFactory : MviStoreFactory {

    private val eventsSubject = BehaviorSubject.createDefault(MviTimeTravelEvents())
    private val statesSubject = BehaviorSubject.createDefault<MviTimeTravelState>(MviTimeTravelState.IDLE)
    private val postponedEvents = ArrayList<MviTimeTravelEvent>()
    private val stores = HashMap<String, MviTimeTravelStore<*, *, *, *, *>>()

    private var state: MviTimeTravelState
        get() = statesSubject.value
        set(value) {
            statesSubject.onNext(value)
        }

    private var currentEvents: MviTimeTravelEvents
        get() = eventsSubject.value
        set(value) {
            eventsSubject.onNext(value)
        }

    /**
     * Observable of time travel state, see [MviTimeTravelState] for more information
     */
    val states: Observable<MviTimeTravelState> = statesSubject

    /**
     * Obserable of time travel events, see [MviTimeTravelEvents] for more information
     */
    val events: Observable<MviTimeTravelEvents> = eventsSubject

    override fun <State : Any, Intent : Any, Action : Any, Result : Any, Label : Any> create(
        name: String,
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        intentToAction: (Intent) -> Action,
        executorFactory: () -> MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>
    ): MviStore<State, Intent, Label> =
        MviTimeTravelStore(
            name = name,
            initialState = initialState,
            bootstrapper = bootstrapper,
            intentToAction = intentToAction,
            executorFactory = executorFactory,
            reducer = reducer
        )
            .also { initStore(it, name) }

    @SuppressLint("CheckResult")
    @VisibleForTesting
    internal fun <State : Any, Intent : Any, Action : Any, Result : Any, Label : Any> initStore(
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
            state = if (currentEvents.items.isNotEmpty()) MviTimeTravelState.STOPPED else MviTimeTravelState.IDLE
        }
    }

    /**
     * Moves to the beginning of the events (right before first event, event index will be -1)
     */
    @MainThread
    fun moveToStart() {
        if (state === MviTimeTravelState.STOPPED) {
            move(currentEvents, -1)
        }
    }

    /**
     * Steps to the previous STATE event, or to the beginning of the events (right before first event, event index will be -1)
     */
    @MainThread
    fun stepBackward() {
        if (state === MviTimeTravelState.STOPPED) {
            step(currentEvents, false)
        }
    }

    /**
     * Steps to the next STATE event
     */
    @MainThread
    fun stepForward() {
        if (state === MviTimeTravelState.STOPPED) {
            step(currentEvents, true)
        }
    }

    /**
     * Moves to the end of the events
     */
    @MainThread
    fun moveToEnd() {
        if (state === MviTimeTravelState.STOPPED) {
            move(currentEvents, currentEvents.items.lastIndex)
        }
    }

    /**
     * Cancels time travel session and switches to IDLE state
     */
    @MainThread
    fun cancel() {
        if (state !== MviTimeTravelState.IDLE) {
            currentEvents = MviTimeTravelEvents()
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
                currentEvents = currentEvents.copy(
                    items = currentEvents.items + event,
                    index = currentEvents.items.size
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
            if ((event.type === MviEventType.STATE) && !set.contains(event.storeName)) {
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
            currentEvents = events.copy(index = to)
        }
    }

    private fun process(event: MviTimeTravelEvent, previousValue: Any? = null) {
        stores[event.storeName]?.eventProcessor?.process(event.type, previousValue ?: event.value)
    }
}