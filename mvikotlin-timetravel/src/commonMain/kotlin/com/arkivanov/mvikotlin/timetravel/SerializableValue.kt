package com.arkivanov.mvikotlin.timetravel

import kotlinx.serialization.SerializationStrategy

class SerializableValue<T : Any>(
    val value: T,
    val serializer: SerializationStrategy<T>?,
)
