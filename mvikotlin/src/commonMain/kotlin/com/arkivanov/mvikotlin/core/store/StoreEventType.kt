package com.arkivanov.mvikotlin.core.store

enum class StoreEventType {

    INTENT, ACTION, RESULT, STATE, LABEL;

    companion object {
        val ALL: Set<StoreEventType> = values().toSet()
    }
}
