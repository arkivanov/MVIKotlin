package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.Value

data class TimeTravelEvent(
    val id: Long,
    val storeName: String,
    val type: StoreEventType,
    val value: Value
)
