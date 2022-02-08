package com.arkivanov.mvikotlin.timetravel

internal fun <T> jsObject(builder: T.() -> Unit): T =
    js("{}").unsafeCast<T>().apply(builder)
