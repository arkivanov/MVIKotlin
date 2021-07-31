package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

@Suppress("EmptyDefaultConstructor")
expect class ValueParser() {

    fun parseValue(obj: Any): ValueNode

    fun parseType(obj: Any): String
}
