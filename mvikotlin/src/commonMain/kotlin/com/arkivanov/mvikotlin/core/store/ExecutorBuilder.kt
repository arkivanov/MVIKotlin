package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi

abstract class ExecutorBuilder<Scope : Any, Intent : Any, Action : Any, State : Any, Message : Any, Label : Any> {

    internal val intentExecutionHandlers = ArrayList<ExecutionHandler<Intent, Scope>>()
    internal val actionExecutionHandlers = ArrayList<ExecutionHandler<Action, Scope>>()


    fun onIntent(
        executionHandler: ExecutionHandler<Intent, Scope>
    ) {
        intentExecutionHandlers += executionHandler
    }

    fun onAction(
        executionHandler: ExecutionHandler<Action, Scope>
    ) {
        actionExecutionHandlers += executionHandler
    }


}
