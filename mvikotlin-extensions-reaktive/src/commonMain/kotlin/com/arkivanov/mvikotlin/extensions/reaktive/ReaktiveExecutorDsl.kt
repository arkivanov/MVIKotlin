package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.atomic
import com.arkivanov.mvikotlin.core.utils.internal.initialize
import com.arkivanov.mvikotlin.core.utils.internal.requireValue
import com.badoo.reaktive.disposable.scope.DisposableScope

/**
 * Creates Reaktive [Executor] factory, which calls the provided [block] for every new instance of the [Executor].
 *
 * @param block configures the [Executor], called for every new instance.
 */
@ExperimentalMviKotlinApi
fun <Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> reaktiveExecutorFactory(
    block: ExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
): () -> Executor<Intent, Action, State, Message, Label> =
    {
        executor(block)
    }

@ExperimentalMviKotlinApi
private fun <Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> executor(
    block: ExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
): Executor<Intent, Action, State, Message, Label> {
    val builder = ExecutorBuilder<Intent, Action, State, Message, Label>()
    block(builder)

    return ExecutorImpl(
        intentHandlers = builder.intentHandlers,
        actionHandlers = builder.actionHandlers,
    )
}

@ExperimentalMviKotlinApi
@ReaktiveExecutorDslMaker
class ExecutorBuilder<Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> internal constructor() {

    @PublishedApi
    internal val intentHandlers = ArrayList<ReaktiveExecutorScope<State, Message, Action, Label>.(Intent) -> Boolean>()

    @PublishedApi
    internal val actionHandlers = ArrayList<ReaktiveExecutorScope<State, Message, Action, Label>.(Action) -> Boolean>()

    /**
     * Registers the provided [Intent] ``[handler] for the given [Intent] type [T].
     * The type is checked using *`is`* operator, so it is possible to use base or `sealed` interfaces or classes.
     */
    inline fun <reified T : Intent> onIntent(
        noinline handler: ReaktiveExecutorScope<State, Message, Action, Label>.(intent: T) -> Unit,
    ) {
        intentHandlers +=
            { intent ->
                if (intent is T) {
                    handler(intent)
                    true
                } else {
                    false
                }
            }
    }

    /**
     * Registers the provided [Action] ``[handler] for the given [Action] type [T].
     * The type is checked using *`is`* operator, so it is possible to use base or `sealed` interfaces or classes.
     */
    inline fun <reified T : Action> onAction(
        noinline handler: ReaktiveExecutorScope<State, Message, Action, Label>.(action: T) -> Unit,
    ) {
        actionHandlers +=
            { action ->
                if (action is T) {
                    handler(action)
                    true
                } else {
                    false
                }
            }
    }
}

@ExperimentalMviKotlinApi
private class ExecutorImpl<in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    private val intentHandlers: List<ReaktiveExecutorScope<State, Message, Action, Label>.(Intent) -> Boolean>,
    private val actionHandlers: List<ReaktiveExecutorScope<State, Message, Action, Label>.(Action) -> Boolean>,
    private val scope: DisposableScope = DisposableScope(),
) : Executor<Intent, Action, State, Message, Label>, ReaktiveExecutorScope<State, Message, Action, Label>, DisposableScope by scope {

    private val callbacks = atomic<Executor.Callbacks<State, Message, Action, Label>>()
    override val state: State get() = callbacks.requireValue().state

    override fun init(callbacks: Executor.Callbacks<State, Message, Action, Label>) {
        this.callbacks.initialize(callbacks)
    }

    override fun executeAction(action: Action) {
        for (handler in actionHandlers) {
            if (handler(this, action)) {
                break
            }
        }
    }

    override fun executeIntent(intent: Intent) {
        for (handler in intentHandlers) {
            if (handler(this, intent)) {
                break
            }
        }
    }

    override fun dispose() {
        scope.dispose()
    }

    override fun dispatch(message: Message) {
        callbacks.requireValue().onMessage(message)
    }

    override fun forward(action: Action) {
        callbacks.requireValue().onAction(action)
    }

    override fun publish(label: Label) {
        callbacks.requireValue().onLabel(label)
    }
}
