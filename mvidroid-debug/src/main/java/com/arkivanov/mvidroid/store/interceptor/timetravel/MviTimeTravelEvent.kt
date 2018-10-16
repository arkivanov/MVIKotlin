package com.arkivanov.mvidroid.store.interceptor.timetravel

import com.arkivanov.mvidroid.store.MviEventType

/**
 * Describes a recorded time travel event
 *
 * @param storeName a name of Store where event was recorded
 * @param type type of the event
 * @param value value of the event
 * @param state State of Store at the moment of the event
 */
data class MviTimeTravelEvent(
    val storeName: String,
    val type: MviEventType,
    val value: Any,
    val state: Any
)
