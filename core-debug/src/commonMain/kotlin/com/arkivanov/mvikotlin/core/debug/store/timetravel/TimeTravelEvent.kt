package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.store.Store

/**
 * Describes a recorded time travel event
 *
 * @param storeName a name of a [Store] where event was recorded
 * @param type type of the event, see [StoreEventType]
 * @param value value of the event
 * @param state State of the [Store] at the moment of the event
 */
data class TimeTravelEvent(
    val storeName: String,
    val type: StoreEventType,
    val value: Any,
    val state: Any
)
