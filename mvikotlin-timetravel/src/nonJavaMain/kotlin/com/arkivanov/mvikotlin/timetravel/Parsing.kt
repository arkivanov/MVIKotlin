package com.arkivanov.mvikotlin.timetravel

internal actual fun Any.parseType(): String =
    this::class.simpleName ?: "Any?"
