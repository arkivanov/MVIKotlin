package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

actual fun parseObject(obj: Any?): Value =
    if (obj == null) {
        Value.Object.Unparsed(type = "Any?", value = "null")
    } else {
        Value.Object.Unparsed(type = obj::class.simpleName ?: "Any?", value = obj.toString())
    }
