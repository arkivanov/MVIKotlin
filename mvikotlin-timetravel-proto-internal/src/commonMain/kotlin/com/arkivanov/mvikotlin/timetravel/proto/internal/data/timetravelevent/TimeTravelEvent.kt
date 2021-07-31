package com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType

data class TimeTravelEvent(
    val id: Long,
    val storeName: String,
    val type: StoreEventType,
    val valueType: String
)
