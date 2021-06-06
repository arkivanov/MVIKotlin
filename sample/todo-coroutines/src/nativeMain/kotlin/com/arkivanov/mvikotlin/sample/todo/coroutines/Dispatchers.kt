package com.arkivanov.mvikotlin.sample.todo.coroutines

import kotlinx.coroutines.CoroutineDispatcher

internal actual val mainDispatcher: CoroutineDispatcher get() = throw NotImplementedError()

internal actual val ioDispatcher: CoroutineDispatcher get() = throw NotImplementedError()
