package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode

data class TimeTravelEventValue(
    val eventId: Long,
    val value: ValueNode
) : ProtoObject
