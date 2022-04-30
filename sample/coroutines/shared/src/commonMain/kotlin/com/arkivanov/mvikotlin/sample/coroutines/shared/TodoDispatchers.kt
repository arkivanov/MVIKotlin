package com.arkivanov.mvikotlin.sample.coroutines.shared

import kotlinx.coroutines.CoroutineDispatcher

interface TodoDispatchers {

    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
