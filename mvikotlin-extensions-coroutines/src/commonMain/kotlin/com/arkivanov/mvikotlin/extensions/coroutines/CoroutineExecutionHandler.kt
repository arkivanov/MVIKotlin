package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.ExecutionHandler
import com.arkivanov.mvikotlin.core.store.directExecutionHandler
import com.arkivanov.mvikotlin.core.store.typedExecutionHandler
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalMviKotlinApi::class)
inline fun <reified T : Any, Action : Any, State : Any, Message : Any, Label : Any> coroutineExecutionHandler(
    noinline handler: CoroutineExecutorScope<State, Message, Action, Label>.(T) -> Unit
): ExecutionHandler<T, CoroutineExecutorScope<State, Message, Action, Label>> {
    return typedExecutionHandler(directExecutionHandler(handler))
}

@OptIn(ExperimentalMviKotlinApi::class)
inline fun <reified T : Any, Action : Any, State : Any, Message : Any, Label : Any> coroutineSkippingExecutionHandler(
    noinline handler: CoroutineExecutorScope<State, Message, Action, Label>.(T) -> Unit
): ExecutionHandler<T, CoroutineExecutorScope<State, Message, Action, Label>> {
    return typedExecutionHandler(CoroutineSkippingExecutionHandler(handler))
}

@OptIn(ExperimentalMviKotlinApi::class)
class CoroutineSkippingExecutionHandler<T : Any, State : Any, Message : Any, Action : Any, Label : Any> @PublishedApi internal constructor(
    private val nestedHandler: CoroutineExecutorScope<State, Message, Action, Label>.(T) -> Unit,
) : ExecutionHandler<T, CoroutineExecutorScope<State, Message, Action, Label>> {

    private var jobHost: Job = Job()

    override fun handle(scope: CoroutineExecutorScope<State, Message, Action, Label>, value: T): Boolean {
        if (jobHost.children.any()) {
            return false
        }
        val newScope = CoroutineSkippingExecutorScope(jobHost, scope)
        nestedHandler.invoke(newScope, value)
        return true
    }

}


@OptIn(ExperimentalMviKotlinApi::class)
private class CoroutineSkippingExecutorScope<State : Any, Message : Any, Action : Any, Label : Any>(
    jobHost: Job,
    private val parentScope: CoroutineExecutorScope<State, Message, Action, Label>,
) : CoroutineExecutorScope<State, Message, Action, Label> by parentScope {

    override val coroutineContext: CoroutineContext = parentScope.coroutineContext + jobHost

}
