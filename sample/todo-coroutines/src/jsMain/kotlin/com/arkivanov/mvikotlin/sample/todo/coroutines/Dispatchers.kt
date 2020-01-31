package com.arkivanov.mvikotlin.sample.todo.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val mainDispatcher: CoroutineDispatcher = Dispatchers.Main

internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
