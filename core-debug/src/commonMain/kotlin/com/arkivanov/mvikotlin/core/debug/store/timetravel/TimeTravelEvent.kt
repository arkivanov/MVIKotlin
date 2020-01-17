package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.debug.store.StoreEventType

/**
 * Describes a recorded time travel event
 *
 * @param storeName a name of Store where event was recorded
 * @param type type of the event
 * @param value value of the event
 * @param state State of Store at the moment of the event
 */
data class TimeTravelEvent(
    val storeName: String,
    val type: StoreEventType,
    val value: Any?,
    val state: Any?
)
