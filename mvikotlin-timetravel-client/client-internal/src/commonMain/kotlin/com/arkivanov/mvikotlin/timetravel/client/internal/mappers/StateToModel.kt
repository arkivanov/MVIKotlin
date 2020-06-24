package com.arkivanov.mvikotlin.timetravel.client.internal.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientStore.State
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Model
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.type

internal val stateToModel: State.() -> Model =
    {
        when (this) {
            is State.Disconnected,
            is State.Connecting ->
                Model(
                    events = emptyList(),
                    currentEventIndex = -1,
                    buttons = toButtons(),
                    selectedEventIndex = -1,
                    selectedEventValue = null
                )

            is State.Connected ->
                Model(
                    events = events.map(TimeTravelEvent::text),
                    currentEventIndex = currentEventIndex,
                    buttons = toButtons(),
                    selectedEventIndex = selectedEvent?.index ?: -1,
                    selectedEventValue = selectedEvent?.event?.value
                )
        }
    }

private val TimeTravelEvent.text: String get() = "[$storeName]: ${type.title}.${value.type}"

private fun State.toButtons(): Model.Buttons =
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
        isDebugEventEnabled = isDebuggableEventSelected()
    )

private fun State.isModeIdle(): Boolean =
    when (this) {
        is State.Disconnected,
        is State.Connecting -> false
        is State.Connected -> mode == TimeTravelStateUpdate.Mode.IDLE
    }

private fun State.isModeRecording(): Boolean =
    when (this) {
        is State.Disconnected,
        is State.Connecting -> false
        is State.Connected -> mode == TimeTravelStateUpdate.Mode.RECORDING
    }

private fun State.isModeStopped(): Boolean =
    when (this) {
        is State.Disconnected,
        is State.Connecting -> false
        is State.Connected -> mode == TimeTravelStateUpdate.Mode.STOPPED
    }

private fun State.isDisconnected(): Boolean =
    when (this) {
        is State.Disconnected -> true
        is State.Connecting,
        is State.Connected -> false
    }

private fun State.isDebuggableEventSelected(): Boolean =
    when (this) {
        is State.Disconnected,
        is State.Connecting -> false
        is State.Connected -> selectedEvent?.event?.type?.isDebuggable() == true
    }

private fun StoreEventType.isDebuggable(): Boolean =
    when (this) {
        StoreEventType.INTENT,
        StoreEventType.ACTION,
        StoreEventType.RESULT,
        StoreEventType.LABEL -> true
        StoreEventType.STATE -> false
    }
