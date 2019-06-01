package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import java.io.Serializable

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
) : Serializable {

    private companion object {
        private const val serialVersionUID = 1L
    }
}
