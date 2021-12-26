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
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.doOnBeforeFinally
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent as TimeTravelEventProto

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

    private sealed class Msg {
        data class Connecting(val disposable: Disposable) : Msg()
        data class Connected(val writer: (TimeTravelCommand) -> Unit) : Msg()
        object Disconnected : Msg()
        data class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Msg()
        data class EventSelected(val index: Int) : Msg()
        data class EventValue(val eventId: Long, val value: ValueNode) : Msg()
        data class ErrorChanged(val text: String?) : Msg()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Msg, Label>() {
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
                is Intent.SelectEvent -> selectEvent(intent.index, getState())
                is Intent.ExportEvents -> sendIfNeeded(getState()) { TimeTravelCommand.ExportEvents }
                is Intent.ImportEvents -> sendIfNeeded(getState()) { TimeTravelCommand.ImportEvents(intent.data) }
                is Intent.RaiseError -> dispatch(Msg.ErrorChanged(text = intent.errorText))
                is Intent.DismissError -> dispatch(Msg.ErrorChanged(text = null))
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
                .doOnBeforeSubscribe { dispatch(Msg.Connecting(it)) }
                .doOnBeforeFinally { dispatch(Msg.Disconnected) }
                .subscribeScoped(onNext = ::onEvent)
        }

        private fun onEvent(event: Connector.Event): Unit =
            when (event) {
                is Connector.Event.Connected -> dispatch(Msg.Connected(event.writer))
                is Connector.Event.StateUpdate -> dispatch(Msg.StateUpdate(event.stateUpdate))
                is Connector.Event.EventValue -> dispatch(Msg.EventValue(eventId = event.eventId, value = event.value))
                is Connector.Event.ExportEvents -> publish(Label.ExportEvents(event.data))
                is Connector.Event.Error -> dispatch(Msg.ErrorChanged(text = event.text))
            }

        private fun disconnectIfNeeded(state: State) {
            val disposable =
                when (val connection = state.connection) {
                    is Connection.Disconnected -> return
                    is Connection.Connecting -> connection.disposable
                    is Connection.Connected -> connection.disposable
                }

            disposable.dispose()
            dispatch(Msg.Disconnected)
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
                events
                    .getOrNull(selectedEventIndex)
                    ?.id
                    ?.let(TimeTravelCommand::DebugEvent)
            }
        }

        private fun selectEvent(index: Int, state: State) {
            dispatch(Msg.EventSelected(index = index))

            sendIfNeeded(state) {
                events
                    .getOrNull(index)
                    ?.takeIf { it.value == null }
                    ?.id
                    ?.let(TimeTravelCommand::AnalyzeEvent)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.Connecting -> copy(connection = Connection.Connecting(disposable = msg.disposable))
                is Msg.Connected -> copy(connection = connection.applyConnected(msg))
                is Msg.Disconnected -> copy(connection = Connection.Disconnected)
                is Msg.StateUpdate -> copy(connection = connection.applyStateUpdate(msg))
                is Msg.EventSelected -> copy(connection = connection.applyEventSelected(msg))
                is Msg.EventValue -> copy(connection = connection.applyEventValue(msg))
                is Msg.ErrorChanged -> copy(errorText = msg.text)
            }

        private fun Connection.applyConnected(msg: Msg.Connected): Connection =
            when (this) {
                is Connection.Disconnected,
                is Connection.Connected -> this
                is Connection.Connecting -> Connection.Connected(disposable = disposable, writer = msg.writer)
            }

        private fun Connection.applyStateUpdate(msg: Msg.StateUpdate): Connection =
            when (this) {
                is Connection.Disconnected,
                is Connection.Connecting -> this
                is Connection.Connected -> applyUpdate(msg.stateUpdate)
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
                is TimeTravelEventsUpdate.All -> update.events.map { it.toDomain() }
                is TimeTravelEventsUpdate.New -> this + update.events.map { it.toDomain() }
            }

        private fun TimeTravelEventProto.toDomain(): TimeTravelEvent =
            TimeTravelEvent(
                id = id,
                storeName = storeName,
                type = type,
                valueType = valueType,
                value = null
            )

        private fun Connection.applyEventSelected(msg: Msg.EventSelected): Connection =
            when (this) {
                is Connection.Disconnected,
                is Connection.Connecting -> this
                is Connection.Connected -> copy(selectedEventIndex = msg.index)
            }

        private fun Connection.applyEventValue(msg: Msg.EventValue): Connection =
            when (this) {
                is Connection.Disconnected,
                is Connection.Connecting -> this

                is Connection.Connected ->
                    copy(
                        events = events.map { event ->
                            event.takeIf { it.id == msg.eventId }?.copy(value = msg.value) ?: event
                        }
                    )
            }
    }

    interface Connector {
        @EventsOnMainScheduler
        fun connect(): Observable<Event>

        sealed class Event {
            class Connected(val writer: (TimeTravelCommand) -> Unit) : Event()
            class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Event()
            class EventValue(val eventId: Long, val value: ValueNode) : Event()
            class ExportEvents(val data: ByteArray) : Event()
            class Error(val text: String?) : Event()
        }
    }
}
