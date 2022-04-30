package com.arkivanov.mvikotlin.sample.coroutines.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.test(): MutableList<T> {
    val list = ArrayList<T>()
    @Suppress("OPT_IN_USAGE")
    GlobalScope.launch(Dispatchers.Unconfined) { collect { list += it } }

    return list
}
