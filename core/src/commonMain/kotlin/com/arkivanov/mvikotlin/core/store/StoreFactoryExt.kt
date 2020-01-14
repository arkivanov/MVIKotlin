package com.arkivanov.mvikotlin.core.store

fun <Intent, State, Label> StoreFactory.create(initialState: State, reducer: Reducer<State, Intent>): Store<Intent, State, Label> =
    create(
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
