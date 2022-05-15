package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue
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
    block: ExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
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
    block: ExecutorBuilder<Intent, Action, State, Message, Label>.() -> Unit,
): Executor<Intent, Action, State, Message, Label> {
    val builder = ExecutorBuilder<Intent, Action, State, Message, Label>()
    block(builder)

    return ExecutorImpl(
        scope = CoroutineScope(mainContext),
        intentHandlers = builder.intentHandlers,
        actionHandlers = builder.actionHandlers,
    )
}

@ExperimentalMviKotlinApi
@CoroutineExecutorDslMaker
class ExecutorBuilder<Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> internal constructor() {

    @PublishedApi
    internal val intentHandlers = ArrayList<CoroutineExecutorScope<State, Message, Label>.(Intent) -> Boolean>()

    @PublishedApi
    internal val actionHandlers = ArrayList<CoroutineExecutorScope<State, Message, Label>.(Action) -> Boolean>()

    /**
     * Registers the provided [Intent] ``[handler] for the given [Intent] type [T].
     * The type is checked using *`is`* operator, so it is possible to use base or `sealed` interfaces or classes.
     */
    inline fun <reified T : Intent> onIntent(noinline handler: CoroutineExecutorScope<State, Message, Label>.(intent: T) -> Unit) {
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
    inline fun <reified T : Action> onAction(noinline handler: CoroutineExecutorScope<State, Message, Label>.(action: T) -> Unit) {
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
private class ExecutorImpl<in Intent : Any, in Action : Any, State : Any, Message : Any, Label : Any>(
    private val scope: CoroutineScope,
    private val intentHandlers: List<CoroutineExecutorScope<State, Message, Label>.(Intent) -> Boolean>,
    private val actionHandlers: List<CoroutineExecutorScope<State, Message, Label>.(Action) -> Boolean>,
) : Executor<Intent, Action, State, Message, Label>, CoroutineExecutorScope<State, Message, Label>, CoroutineScope by scope {

    private val callbacks = atomic<Executor.Callbacks<State, Message, Label>>()
    override val state: State get() = callbacks.requireValue().state

    override fun init(callbacks: Executor.Callbacks<State, Message, Label>) {
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
        scope.cancel()
    }

    override fun dispatch(message: Message) {
        callbacks.requireValue().onMessage(message)
    }

    override fun publish(label: Label) {
        callbacks.requireValue().onLabel(label)
    }
}
