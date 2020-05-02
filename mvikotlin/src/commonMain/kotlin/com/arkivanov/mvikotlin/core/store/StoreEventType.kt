package com.arkivanov.mvikotlin.core.store

enum class StoreEventType {

    INTENT, ACTION, RESULT, STATE, LABEL;

    val title: String = name.toLowerCase().capitalize()

    companion object {
        val VALUES: Set<StoreEventType> = values().toSet()
    }
}
