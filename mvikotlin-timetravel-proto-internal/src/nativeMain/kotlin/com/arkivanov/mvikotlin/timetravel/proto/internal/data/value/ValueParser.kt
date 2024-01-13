package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

@Suppress("EmptyDefaultConstructor")
actual class ValueParser actual constructor() {

    actual fun parseType(obj: Any): String = obj::class.simpleName ?: "Any?"
}
