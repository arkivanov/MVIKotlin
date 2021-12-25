package com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient.Model
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.State
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode

internal val stateToModel: State.() -> Model =
    {
        when (connection) {
            is State.Connection.Disconnected,
            is State.Connection.Connecting -> emptyModel(buttons = connection.toButtons(), errorText = errorText)
            is State.Connection.Connected -> connectedModel(connection = connection, errorText = errorText)
        }
    }

private fun emptyModel(buttons: Model.Buttons, errorText: String?): Model =
    Model(
        events = emptyList(),
        currentEventIndex = -1,
        buttons = buttons,
        selectedEventIndex = -1,
        selectedEventValue = null,
        errorText = errorText
    )

private fun connectedModel(connection: State.Connection.Connected, errorText: String?): Model =
    Model(
        events = connection.events.map(TimeTravelEvent::text),
        currentEventIndex = connection.currentEventIndex,
        buttons = connection.toButtons(),
        selectedEventIndex = connection.selectedEventIndex,
        selectedEventValue = connection.events.getOrNull(connection.selectedEventIndex)?.let { it.value ?: ValueNode(type = "...") },
        errorText = errorText
    )

private val TimeTravelEvent.text: String get() = "[$storeName]: ${type.title}.$valueType"

private fun State.Connection.toButtons(): Model.Buttons =
    Model.Buttons(
        isConnectEnabled = isDisconnected(),
        isDisconnectEnabled = !isDisconnected(),
        isStartRecordingEnabled = isModeIdle(),
        isStopRecordingEnabled = isModeRecording(),
        isMoveToStartEnabled = isModeStopped(),
        isStepBackwardEnabled = isModeStopped(),
        isStepForwardEnabled = isModeStopped(),
        isMoveToEndEnabled = isModeStopped(),
        isCancelEnabled = isModeRecording() || isModeStopped(),
        isDebugEventEnabled = isDebuggableEventSelected(),
        isExportEventsEnabled = isModeStopped(),
        isImportEventsEnabled = isModeIdle()
    )

private fun State.Connection.isModeIdle(): Boolean =
    when (this) {
        is State.Connection.Disconnected,
        is State.Connection.Connecting -> false
        is State.Connection.Connected -> mode == TimeTravelStateUpdate.Mode.IDLE
    }

private fun State.Connection.isModeRecording(): Boolean =
    when (this) {
        is State.Connection.Disconnected,
        is State.Connection.Connecting -> false
        is State.Connection.Connected -> mode == TimeTravelStateUpdate.Mode.RECORDING
    }

private fun State.Connection.isModeStopped(): Boolean =
    when (this) {
        is State.Connection.Disconnected,
        is State.Connection.Connecting -> false
        is State.Connection.Connected -> mode == TimeTravelStateUpdate.Mode.STOPPED
    }

private fun State.Connection.isDisconnected(): Boolean =
    when (this) {
        is State.Connection.Disconnected -> true
        is State.Connection.Connecting,
        is State.Connection.Connected -> false
    }

private fun State.Connection.isDebuggableEventSelected(): Boolean =
    when (this) {
        is State.Connection.Disconnected,
        is State.Connection.Connecting -> false
        is State.Connection.Connected -> events.getOrNull(selectedEventIndex)?.type?.isDebuggable() == true
    }

private fun StoreEventType.isDebuggable(): Boolean =
    when (this) {
        StoreEventType.INTENT,
        StoreEventType.ACTION,
        StoreEventType.MESSAGE,
        StoreEventType.LABEL -> true
        StoreEventType.STATE -> false
    }
