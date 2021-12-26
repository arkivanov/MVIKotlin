package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Reducer

fun reducer(reduce: String.(String) -> String = { "${this}_$it" }): Reducer<String, String> =
    Reducer { reduce(it) }
