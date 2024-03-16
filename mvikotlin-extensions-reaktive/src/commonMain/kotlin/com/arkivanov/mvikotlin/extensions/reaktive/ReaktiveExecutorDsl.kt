package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.DslExecutorImpl
import com.arkivanov.mvikotlin.core.store.ExecutionHandler
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.ExecutorBuilder
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.badoo.reaktive.disposable.scope.DisposableScope

/**
 * Creates Reaktive [Executor] factory, which calls the provided [block] for every new instance of the [Executor].
 *
 * @param block configures the [Executor], called for every new instance.
 */
@ExperimentalMviKotlinApi
fun <Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> reaktiveExecutorFactory(
    block: ReactiveExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
): () -> Executor<Intent, Action, State, Message, Label> =
    {
        executor(block)
    }

@ExperimentalMviKotlinApi
private fun <Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> executor(
    block: ReactiveExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
): Executor<Intent, Action, State, Message, Label> {
    val builder = ReactiveExecutorBuilder<Intent, Action, State, Message, Label>()
    block(builder)

    return ReactiveExecutorImpl(
        builder = builder,
    )
}

@ExperimentalMviKotlinApi
@ReaktiveExecutorDslMaker
class ReactiveExecutorBuilder<Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> internal constructor() :
    ExecutorBuilder<ReaktiveExecutorScope<State, Message, Action, Label>, Intent, Action, State, Message, Label>() {

    /**
     * Registers the provided [Intent] ``[handler] for the given [Intent] type [T].
     * The type is checked using *`is`* operator, so it is possible to use base or `sealed` interfaces or classes.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Intent> onIntent(
        noinline handler: ReaktiveExecutorScope<State, Message, Action, Label>.(intent: T) -> Unit,
    ) {
        onIntent(reactiveExecutionHandler(handler) as ExecutionHandler<Intent, ReaktiveExecutorScope<State, Message, Action, Label>>)
    }

    /**
     * Registers the provided [Action] ``[handler] for the given [Action] type [T].
     * The type is checked using *`is`* operator, so it is possible to use base or `sealed` interfaces or classes.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Action> onAction(
        noinline handler: ReaktiveExecutorScope<State, Message, Action, Label>.(action: T) -> Unit,
    ) {
        onAction(reactiveExecutionHandler(handler) as ExecutionHandler<Action, ReaktiveExecutorScope<State, Message, Action, Label>>)
    }
}

@ExperimentalMviKotlinApi
private class ReactiveExecutorImpl<in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    private val scope: DisposableScope = DisposableScope(),
    builder: ExecutorBuilder<ReaktiveExecutorScope<State, Message, Action, Label>, Intent, Action, State, Message, Label>,
) : DslExecutorImpl<ReaktiveExecutorScope<State, Message, Action, Label>, Intent, Action, State, Message, Label>(builder),
    ReaktiveExecutorScope<State, Message, Action, Label>, DisposableScope by scope {

    override fun getScope(): ReaktiveExecutorScope<State, Message, Action, Label> {
        return this
    }

    override fun dispose() {
        scope.dispose()
    }

}
