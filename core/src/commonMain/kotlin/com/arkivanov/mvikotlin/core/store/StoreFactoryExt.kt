package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
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
                private val callbacks = lazyAtomicReference<Executor.Callbacks<State, Intent, Label>>()

                override fun init(callbacks: Executor.Callbacks<State, Intent, Label>) {
                    check(this.callbacks.value == null) { "Executor is already initialized" }

                    this.callbacks.value = callbacks
                }

                override fun handleIntent(intent: Intent) {
                    callbacks.requireValue.onResult(intent)
                }
            }
        },
        reducer = reducer
    )
