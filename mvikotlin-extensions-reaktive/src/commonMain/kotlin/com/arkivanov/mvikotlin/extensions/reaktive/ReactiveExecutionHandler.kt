package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.ExecutionHandler
import com.arkivanov.mvikotlin.core.store.directExecutionHandler
import com.arkivanov.mvikotlin.core.store.typedExecutionHandler
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi

@OptIn(ExperimentalMviKotlinApi::class)
inline fun <reified T : Any, Action : Any, State : Any, Message : Any, Label : Any> reactiveExecutionHandler(
    noinline handler: ReaktiveExecutorScope<State, Message, Action, Label>.(T) -> Unit
): ExecutionHandler<T, ReaktiveExecutorScope<State, Message, Action, Label>> {
    return typedExecutionHandler(directExecutionHandler(handler))
}
