package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueParser
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType as StoreEventTypeProto
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent as TimeTravelEventProto
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate as TimeTravelStateUpdateProto

internal fun List<TimeTravelEvent>.toProto(): List<TimeTravelEventProto> {
    val valueParser = ValueParser()

    return map { it.toProto(valueParser) }
}

private fun TimeTravelEvent.toProto(valueParser: ValueParser): TimeTravelEventProto =
    TimeTravelEventProto(
        id = id,
        storeName = storeName,
        type = type.toProto(),
        valueType = valueParser.parseType(obj = value)
    )

private fun StoreEventType.toProto(): StoreEventTypeProto =
    when (this) {
        StoreEventType.INTENT -> StoreEventTypeProto.INTENT
        StoreEventType.ACTION -> StoreEventTypeProto.ACTION
        StoreEventType.MESSAGE -> StoreEventTypeProto.MESSAGE
        StoreEventType.STATE -> StoreEventTypeProto.STATE
        StoreEventType.LABEL -> StoreEventTypeProto.LABEL
    }

internal fun TimeTravelState.Mode.toProto(): TimeTravelStateUpdateProto.Mode =
    when (this) {
        TimeTravelState.Mode.IDLE -> TimeTravelStateUpdateProto.Mode.IDLE
        TimeTravelState.Mode.RECORDING -> TimeTravelStateUpdateProto.Mode.RECORDING
        TimeTravelState.Mode.STOPPED -> TimeTravelStateUpdateProto.Mode.STOPPED
    }
