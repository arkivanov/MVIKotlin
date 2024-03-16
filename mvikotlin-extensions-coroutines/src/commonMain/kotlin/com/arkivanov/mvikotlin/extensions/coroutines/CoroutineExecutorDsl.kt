package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.DslExecutorImpl
import com.arkivanov.mvikotlin.core.store.ExecutionHandler
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.ExecutorBuilder
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Creates coroutine [Executor] factory, which calls the provided [block] for every new instance of the [Executor].
 *
 * @param mainContext main [CoroutineContext] to be used by default when launching coroutines, default value is [Dispatchers.Main].
 * @param block configures the [Executor], called for every new instance.
 */
@ExperimentalMviKotlinApi
fun <Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> coroutineExecutorFactory(
    mainContext: CoroutineContext = Dispatchers.Main,
    block: CoroutineExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
): () -> Executor<Intent, Action, State, Message, Label> =
    {
        executor(
            mainContext = mainContext,
            block = block,
        )
    }

@ExperimentalMviKotlinApi
private fun <Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> executor(
    mainContext: CoroutineContext,
    block: CoroutineExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
): Executor<Intent, Action, State, Message, Label> {
    val builder = CoroutineExecutorBuilder<Intent, Action, State, Message, Label>()
    block(builder)

    return CoroutineDslExecutorImpl(
        scope = CoroutineScope(mainContext),
        builder = builder,
    )
}

@ExperimentalMviKotlinApi
@CoroutineExecutorDslMaker
class CoroutineExecutorBuilder<Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> internal constructor() :
    ExecutorBuilder<CoroutineExecutorScope<State, Message, Action, Label>, Intent, Action, State, Message, Label>() {

    /**
     * Registers the provided [Intent] ``[handler] for the given [Intent] type [T].
     * The type is checked using *`is`* operator, so it is possible to use base or `sealed` interfaces or classes.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Intent> onIntent(
        noinline handler: CoroutineExecutorScope<State, Message, Action, Label>.(intent: T) -> Unit,
    ) {
        onIntent(
            executionHandler = coroutineExecutionHandler(handler) as ExecutionHandler<Intent, CoroutineExecutorScope<State, Message, Action, Label>>
        )
    }

    /**
     * Registers the provided [Action] ``[handler] for the given [Action] type [T].
     * The type is checked using *`is`* operator, so it is possible to use base or `sealed` interfaces or classes.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Action> onAction(
        noinline handler: CoroutineExecutorScope<State, Message, Action, Label>.(action: T) -> Unit,
    ) {
        onAction(
            executionHandler = coroutineExecutionHandler(handler) as ExecutionHandler<Action, CoroutineExecutorScope<State, Message, Action, Label>>
        )
    }
}

@ExperimentalMviKotlinApi
private class CoroutineDslExecutorImpl<in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    private val scope: CoroutineScope,
    builder: ExecutorBuilder<CoroutineExecutorScope<State, Message, Action, Label>, Intent, Action, State, Message, Label>
) : DslExecutorImpl<CoroutineExecutorScope<State, Message, Action, Label>, Intent, Action, State, Message, Label>(builder),
    CoroutineExecutorScope<State, Message, Action, Label>, CoroutineScope by scope {

    override fun getScope(): CoroutineExecutorScope<State, Message, Action, Label> {
        return this
    }

    override fun dispose() {
        scope.cancel()
    }

}
