package com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype

enum class StoreEventType {

    INTENT, ACTION, RESULT, STATE, LABEL;

    val title: String = name.toLowerCase().capitalize()
}
