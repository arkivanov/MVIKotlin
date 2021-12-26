package com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype

enum class StoreEventType {

    INTENT, ACTION, MESSAGE, STATE, LABEL;

    val title: String = name.lowercase().replaceFirstChar { it.uppercase() }
}
