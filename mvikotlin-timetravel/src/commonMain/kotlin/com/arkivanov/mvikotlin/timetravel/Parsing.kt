package com.arkivanov.mvikotlin.timetravel

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
import kotlinx.serialization.json.Json

private val json =
    Json {
        allowStructuredMapKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

internal expect fun Any.parseType(): String

internal fun <T : Any> SerializableValue<T>.parse(): ValueNode =
    ValueNode(
        type = value.parseType(),
        value = json.encodeToString(serializer, value),
    )
