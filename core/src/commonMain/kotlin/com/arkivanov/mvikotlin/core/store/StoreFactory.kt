package com.arkivanov.mvikotlin.core.store

interface StoreFactory {

    fun <Intent, Action, Result, State, Label> create(
        initialState: State,
        bootstrapper: Bootstrapper<Action>? = null,
        executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
        @Suppress("UNCHECKED_CAST")
        reducer: Reducer<State, Result> = bypassReducer as Reducer<State, Any?>
    ): Store<Intent, State, Label>

    private companion object {
        private val bypassReducer =
            object : Reducer<Any?, Any?> {
                override fun Any?.reduce(result: Any?): Any? = this
            }
    }
}
