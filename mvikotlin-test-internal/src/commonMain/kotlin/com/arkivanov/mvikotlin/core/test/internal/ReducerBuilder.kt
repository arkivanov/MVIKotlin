package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Reducer

fun reducer(reduce: String.(String) -> String = { "${this}_$it" }): Reducer<String, String> =
    object : Reducer<String, String> {
        override fun String.reduce(result: String): String = reduce(result)
    }
