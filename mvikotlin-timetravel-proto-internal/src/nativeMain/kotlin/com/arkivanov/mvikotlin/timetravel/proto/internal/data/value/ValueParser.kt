package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

@Suppress("EmptyDefaultConstructor")
actual class ValueParser actual constructor() {

    actual fun parseValue(obj: Any): ValueNode =
        ValueNode(
            type = parseType(obj = obj),
            value = obj.toString()
        )

    actual fun parseType(obj: Any): String = obj::class.simpleName ?: "Any?"
}
