package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.lateinitAtomicReference
import com.arkivanov.mvikotlin.utils.internal.requireValue

fun <Intent : Any, State : Any, Label : Any> StoreFactory.create(
    name: String,
    initialState: State,
    reducer: Reducer<State, Intent>
): Store<Intent, State, Label> =
    create(
        name = name,
        initialState = initialState,
        executorFactory = {
            object : Executor<Intent, Nothing, State, Intent, Label> {
                private val callbacks = lateinitAtomicReference<Executor.Callbacks<State, Intent, Label>>()

                override fun init(callbacks: Executor.Callbacks<State, Intent, Label>) {
                    this.callbacks.initialize(callbacks)
                }

                override fun handleIntent(intent: Intent) {
                    callbacks.requireValue.onResult(intent)
                }
            }
        },
        reducer = reducer
    )
