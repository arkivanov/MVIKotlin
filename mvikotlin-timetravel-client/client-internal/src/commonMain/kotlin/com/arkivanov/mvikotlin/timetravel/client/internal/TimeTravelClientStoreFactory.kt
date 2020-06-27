package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientStore.Label
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientStore.State
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.doOnBeforeFinally
import com.badoo.reaktive.observable.doOnBeforeSubscribe

internal class TimeTravelClientStoreFactory(
    private val storeFactory: StoreFactory,
    private val connector: Connector
) {

    fun create(): TimeTravelClientStore =
        object : TimeTravelClientStore, Store<Intent, State, Label> by storeFactory.create(
            initialState = State.Disconnected,
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Result {
        class Connecting(val disposable: Disposable) : Result()
        class Connected(val writer: (TimeTravelCommand) -> Unit) : Result()
        object Disconnected : Result()
        class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Result()
        class EventSelected(val event: TimeTravelEvent, val index: Int) : Result()
        object EventUnselected : Result()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Result, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) =
            when (intent) {
                is Intent.Connect -> connectIfNeeded(getState())
                is Intent.Disconnect -> disconnectIfNeeded(getState())
                is Intent.StartRecording -> sendIfNeeded(getState()) { TimeTravelCommand.StartRecording }
                is Intent.StopRecording -> sendIfNeeded(getState()) { TimeTravelCommand.StopRecording }
                is Intent.MoveToStart -> sendIfNeeded(getState()) { TimeTravelCommand.MoveToStart }
                is Intent.StepBackward -> sendIfNeeded(getState()) { TimeTravelCommand.StepBackward }
                is Intent.StepForward -> sendIfNeeded(getState()) { TimeTravelCommand.StepForward }
                is Intent.MoveToEnd -> sendIfNeeded(getState()) { TimeTravelCommand.MoveToEnd }
                is Intent.Cancel -> sendIfNeeded(getState()) { TimeTravelCommand.Cancel }
                is Intent.DebugEvent -> debugEventIfNeeded(getState())
                is Intent.SelectEvent -> selectEventIfNeeded(getState(), intent.index)
            }

        private fun connectIfNeeded(state: State): Unit =
            when (state) {
                is State.Disconnected -> connect()
                is State.Connecting,
                is State.Connected -> Unit
            }

        private fun connect() {
            connector
                .connect()
                .doOnBeforeSubscribe { dispatch(Result.Connecting(it)) }
                .doOnBeforeFinally { dispatch(Result.Disconnected) }
                .subscribeScoped(onNext = ::onEvent)
        }

        private fun onEvent(event: Connector.Event): Unit =
            when (event) {
                is Connector.Event.Connected -> dispatch(Result.Connected(event.writer))
                is Connector.Event.StateUpdate -> dispatch(Result.StateUpdate(event.stateUpdate))
                is Connector.Event.Error -> publish(Label.Error(event.text))
            }

        private fun disconnectIfNeeded(state: State) {
            val disposable =
                when (state) {
                    is State.Disconnected -> return
                    is State.Connecting -> state.disposable
                    is State.Connected -> state.disposable
                }

            disposable.dispose()
            dispatch(Result.Disconnected)
        }

        private inline fun sendIfNeeded(state: State, command: State.Connected.() -> TimeTravelCommand?): Unit =
            when (state) {
                is State.Disconnected,
                is State.Connecting -> Unit
                is State.Connected -> {
                    state.command()?.also(state.writer)
                    Unit
                }
            }

        private fun debugEventIfNeeded(state: State) {
            sendIfNeeded(state) {
                selectedEvent?.event?.id?.let(TimeTravelCommand::DebugEvent)
            }
        }

        private fun selectEventIfNeeded(state: State, index: Int): Unit =
            when (state) {
                is State.Disconnected,
                is State.Connecting -> Unit
                is State.Connected -> selectEvent(state, index)
            }

        private fun selectEvent(state: State.Connected, index: Int) {
            dispatch(
                state
                    .events
                    .getOrNull(index)
                    ?.let { Result.EventSelected(event = it, index = index) }
                    ?: Result.EventUnselected
            )
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Connecting -> State.Connecting(disposable = result.disposable)
                is Result.Connected -> applyConnected(result)
                is Result.Disconnected -> State.Disconnected
                is Result.StateUpdate -> applyStateUpdate(result)
                is Result.EventSelected -> applyEventSelected(result)
                is Result.EventUnselected -> applyEventUnselected()
            }

        private fun State.applyConnected(result: Result.Connected): State =
            when (this) {
                is State.Disconnected,
                is State.Connected -> this
                is State.Connecting -> State.Connected(disposable = disposable, writer = result.writer)
            }

        private fun State.applyStateUpdate(result: Result.StateUpdate): State =
            when (this) {
                is State.Disconnected,
                is State.Connecting -> this
                is State.Connected -> applyUpdate(result.stateUpdate)
            }

        private fun State.Connected.applyUpdate(update: TimeTravelStateUpdate): State.Connected =
            copy(
                events = events.applyUpdate(update = update.eventsUpdate),
                currentEventIndex = update.selectedEventIndex,
                mode = update.mode
            )

        private fun List<TimeTravelEvent>.applyUpdate(update: TimeTravelEventsUpdate): List<TimeTravelEvent> =
            when (update) {
                is TimeTravelEventsUpdate.All -> update.events
                is TimeTravelEventsUpdate.New -> this + update.events
            }

        private fun State.applyEventSelected(result: Result.EventSelected): State =
            when (this) {
                is State.Disconnected,
                is State.Connecting -> this
                is State.Connected -> copy(selectedEvent = State.Connected.SelectedEvent(event = result.event, index = result.index))
            }

        private fun State.applyEventUnselected(): State =
            when (this) {
                is State.Disconnected,
                is State.Connecting -> this
                is State.Connected -> copy(selectedEvent = null)
            }
    }

    interface Connector {
        @EventsOnMainScheduler
        fun connect(): Observable<Event>

        sealed class Event {
            class Connected(val writer: (TimeTravelCommand) -> Unit) : Event()
            class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Event()
            class Error(val text: String?) : Event()
        }
    }
}
