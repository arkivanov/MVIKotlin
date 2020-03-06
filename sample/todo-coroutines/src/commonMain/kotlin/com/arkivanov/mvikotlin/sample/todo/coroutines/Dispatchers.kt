package com.arkivanov.mvikotlin.sample.todo.coroutines

import kotlinx.coroutines.CoroutineDispatcher

internal expect val mainDispatcher: CoroutineDispatcher

internal expect val ioDispatcher: CoroutineDispatcher
