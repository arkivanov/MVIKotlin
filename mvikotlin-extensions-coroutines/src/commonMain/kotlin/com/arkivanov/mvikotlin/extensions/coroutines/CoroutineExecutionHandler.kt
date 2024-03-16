package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.ExecutionHandler
import com.arkivanov.mvikotlin.core.store.directExecutionHandler
import com.arkivanov.mvikotlin.core.store.typedExecutionHandler
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi

@OptIn(ExperimentalMviKotlinApi::class)
inline fun <reified T : Any, Action : Any, State : Any, Message : Any, Label : Any> coroutineExecutionHandler(
    noinline handler: CoroutineExecutorScope<State, Message, Action, Label>.(T) -> Unit
): ExecutionHandler<T, CoroutineExecutorScope<State, Message, Action, Label>> {
    return typedExecutionHandler(directExecutionHandler(handler))
}
