package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

actual fun parseObject(obj: Any?): ParsedValue =
    if (obj == null) {
        ParsedValue.Object.Unparsed(type = "Any?", value = "null")
    } else {
        ParsedValue.Object.Unparsed(type = obj::class.simpleName ?: "Any?", value = obj.toString())
    }
