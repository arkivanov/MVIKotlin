package com.arkivanov.mvikotlin.core.store

fun <Intent : Any, State : Any, Label : Any> StoreFactory.create(
    name: String,
    initialState: State,
    reducer: Reducer<State, Intent>
): Store<Intent, State, Label> =
    create(
        name = name,
        initialState = initialState,
        executorFactory = {
            object : BaseExecutor<Intent, Nothing, State, Intent, Label>() {
                override fun handleIntent(intent: Intent) {
                    super.handleIntent(intent)
                    dispatch(intent)
                }
            }
        },
        reducer = reducer
    )
