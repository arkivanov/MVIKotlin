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

fun <T : Any> parseValue(obj: T, serializer: SerializationStrategy<T>): ValueNode =
    ValueNode(
        type = ValueParser().parseType(obj),
        value = json.encodeToString(serializer, obj),
    )
