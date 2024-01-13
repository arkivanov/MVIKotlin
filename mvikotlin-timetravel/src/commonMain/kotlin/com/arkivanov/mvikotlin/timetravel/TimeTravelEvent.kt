package com.arkivanov.mvikotlin.timetravel

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.parseValue
import kotlinx.serialization.SerializationStrategy

/**
 * Describes a recorded time travel event
 *
 * @param id a unique identifier of the event
 * @param storeName a name of a [Store] where event was recorded
 * @param type type of the event, see [StoreEventType]
 * @param value value of the event
 * @param state `State` of the [Store] at the moment of the event
 */
data class TimeTravelEvent<T : Any, State : Any>(
    val id: Long,
    val storeName: String,
    val type: StoreEventType,
    val value: T,
    val valueSerializer: SerializationStrategy<T>,
    val state: State,
    val stateSerializer: SerializationStrategy<State>,
) : JvmSerializable

internal fun <T : Any> TimeTravelEvent<T, *>.parseValue(): ValueNode =
    parseValue(obj = value, serializer = valueSerializer)
