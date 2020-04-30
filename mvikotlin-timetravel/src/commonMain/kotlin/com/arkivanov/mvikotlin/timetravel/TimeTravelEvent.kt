package com.arkivanov.mvikotlin.timetravel

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.JvmSerializable

/**
 * Describes a recorded time travel event
 *
 * @param id a unique identifier of the event
 * @param storeName a name of a [Store] where event was recorded
 * @param type type of the event, see [StoreEventType]
 * @param value value of the event
 * @param state `State` of the [Store] at the moment of the event
 */
data class TimeTravelEvent(
    val id: Long,
    val storeName: String,
    val type: StoreEventType,
    val value: Any,
    val state: Any
) : JvmSerializable
