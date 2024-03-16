package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.atomic
import com.arkivanov.mvikotlin.core.utils.internal.initialize
import com.arkivanov.mvikotlin.core.utils.internal.requireValue

@InternalMviKotlinApi
@ExperimentalMviKotlinApi
abstract class DslExecutorImpl<Scope : Any, in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    builder: ExecutorBuilder<Scope, Intent, Action, State, Message, Label>,
) : Executor<Intent, Action, State, Message, Label> {

    private val intentHandlers = builder.intentExecutionHandlers
    private val actionHandlers = builder.actionExecutionHandlers

    private val callbacks = atomic<Executor.Callbacks<State, Message, Action, Label>>()

    abstract fun getScope(): Scope

    fun state(): State =
        callbacks.requireValue().state

    override fun init(callbacks: Executor.Callbacks<State, Message, Action, Label>) {
        this.callbacks.initialize(callbacks)
    }

    override fun executeAction(action: Action) {
        for (handler in actionHandlers) {
            if (handler.handle(getScope(), action)) {
                break
            }
        }
    }

    override fun executeIntent(intent: Intent) {
        for (handler in intentHandlers) {
            if (handler.handle(getScope(), intent)) {
                break
            }
        }
    }

    fun dispatch(message: Message) {
        callbacks.requireValue().onMessage(message)
    }

    fun forward(action: Action) {
        callbacks.requireValue().onAction(action)
    }

    fun publish(label: Label) {
        callbacks.requireValue().onLabel(label)
    }

}
