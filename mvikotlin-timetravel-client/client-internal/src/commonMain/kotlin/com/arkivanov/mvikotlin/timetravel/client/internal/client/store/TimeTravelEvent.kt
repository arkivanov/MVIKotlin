package com.arkivanov.mvikotlin.timetravel.client.internal.client.store

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode

internal data class TimeTravelEvent(
    val id: Long,
    val storeName: String,
    val type: StoreEventType,
    val valueType: String,
    val value: ValueNode?
)
