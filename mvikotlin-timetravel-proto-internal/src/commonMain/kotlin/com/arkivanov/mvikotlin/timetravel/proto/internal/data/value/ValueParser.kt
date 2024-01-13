package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

@Suppress("EmptyDefaultConstructor")
expect class ValueParser() {

    fun parseType(obj: Any): String
}

private val json =
    Json {
        allowStructuredMapKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

fun parseValue(obj: Any, serializer: SerializationStrategy<*>): ValueNode =
    ValueNode(
        type = ValueParser().parseType(obj),
        value = json.encodeToString(serializer as SerializationStrategy<Any>, obj),
    )
