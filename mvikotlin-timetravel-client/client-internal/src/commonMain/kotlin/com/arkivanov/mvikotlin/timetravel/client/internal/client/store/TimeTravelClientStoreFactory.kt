package com.arkivanov.mvikotlin.timetravel.client.internal.client.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.Label
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.State
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.State.Connection
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
    private val connector: Connector,
) {

    fun create(): TimeTravelClientStore =
        object : TimeTravelClientStore, Store<Intent, State, Label> by storeFactory.create(
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Result {
        data class Connecting(val disposable: Disposable) : Result()
        data class Connected(val writer: (TimeTravelCommand) -> Unit) : Result()
        object Disconnected : Result()
        data class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Result()
        data class EventSelected(val index: Int) : Result()
        data class ErrorChanged(val text: String?) : Result()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Result, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
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
                is Intent.SelectEvent -> selectEvent(intent.index)
                is Intent.ExportEvents -> sendIfNeeded(getState()) { TimeTravelCommand.ExportEvents }
                is Intent.ImportEvents -> sendIfNeeded(getState()) { TimeTravelCommand.ImportEvents(intent.data) }
                is Intent.RaiseError -> dispatch(Result.ErrorChanged(text = intent.errorText))
                is Intent.DismissError -> dispatch(Result.ErrorChanged(text = null))
            }

        private fun connectIfNeeded(state: State): Unit =
            when (state.connection) {
                is Connection.Disconnected -> connect()
                is Connection.Connecting,
                is Connection.Connected -> Unit
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
                is Connector.Event.ExportEvents -> publish(Label.ExportEvents(event.data))
                is Connector.Event.Error -> dispatch(Result.ErrorChanged(event.text))
            }

        private fun disconnectIfNeeded(state: State) {
            val disposable =
                when (val connection = state.connection) {
                    is Connection.Disconnected -> return
                    is Connection.Connecting -> connection.disposable
                    is Connection.Connected -> connection.disposable
                }

            disposable.dispose()
            dispatch(Result.Disconnected)
        }

        private inline fun sendIfNeeded(state: State, command: Connection.Connected.() -> TimeTravelCommand?): Unit =
            when (val connection = state.connection) {
                is Connection.Disconnected,
                is Connection.Connecting -> Unit
                is Connection.Connected -> {
                    connection.command()?.also(connection.writer)
                    Unit
                }
            }

        private fun debugEventIfNeeded(state: State) {
            sendIfNeeded(state) {
                when (val connection = state.connection) {
                    is Connection.Disconnected,
                    is Connection.Connecting -> null

                    is Connection.Connected ->
                        connection
                            .events
                            .getOrNull(connection.selectedEventIndex)
                            ?.id
                            ?.let(TimeTravelCommand::DebugEvent)
                }
            }
        }

        private fun selectEvent(index: Int) {
            dispatch(Result.EventSelected(index = index))
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Connecting -> copy(connection = Connection.Connecting(disposable = result.disposable))
                is Result.Connected -> copy(connection = connection.applyConnected(result))
                is Result.Disconnected -> copy(connection = Connection.Disconnected)
                is Result.StateUpdate -> copy(connection = connection.applyStateUpdate(result))
                is Result.EventSelected -> copy(connection = connection.applyEventSelected(result))
                is Result.ErrorChanged -> copy(errorText = result.text)
            }

        private fun Connection.applyConnected(result: Result.Connected): Connection =
            when (this) {
                is Connection.Disconnected,
                is Connection.Connected -> this
                is Connection.Connecting -> Connection.Connected(disposable = disposable, writer = result.writer)
            }

        private fun Connection.applyStateUpdate(result: Result.StateUpdate): Connection =
            when (this) {
                is Connection.Disconnected,
                is Connection.Connecting -> this
                is Connection.Connected -> applyUpdate(result.stateUpdate)
            }

        private fun Connection.Connected.applyUpdate(update: TimeTravelStateUpdate): Connection.Connected =
            copy(
                events = events.applyUpdate(update = update.eventsUpdate),
                currentEventIndex = update.selectedEventIndex,
                mode = update.mode,
                selectedEventIndex = selectedEventIndex.coerceAtMost(events.lastIndex)
            )

        private fun List<TimeTravelEvent>.applyUpdate(update: TimeTravelEventsUpdate): List<TimeTravelEvent> =
            when (update) {
                is TimeTravelEventsUpdate.All -> update.events
                is TimeTravelEventsUpdate.New -> this + update.events
            }

        private fun Connection.applyEventSelected(result: Result.EventSelected): Connection =
            when (this) {
                is Connection.Disconnected,
                is Connection.Connecting -> this
                is Connection.Connected -> copy(selectedEventIndex = result.index)
            }
    }

    interface Connector {
        @EventsOnMainScheduler
        fun connect(): Observable<Event>

        sealed class Event {
            class Connected(val writer: (TimeTravelCommand) -> Unit) : Event()
            class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Event()
            class ExportEvents(val data: ByteArray) : Event()
            class Error(val text: String?) : Event()
        }
    }
}
