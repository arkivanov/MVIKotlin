package com.arkivanov.mvidroid.store

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer

/**
 * Store factories are be used to create Store implementations.
 * You can create different Store wrappers and combine them depending on circumstances.
 */
interface MviStoreFactory {

    /**
     * Creates an implementation of Store, must be called only on Main thread
     *
     * @param initialState initial State of Store
     * @param bootstrapper optional Bootstrapper for Store initialization, see [MviBootstrapper]
     * @param intentToAction a function that maps Intents to Actions
     * @param executorFactory A factory of Executor that will execute Store's Actions, see [MviExecutor].
     * In some cases (like debugging using time travel) Executor can be created multiple times.
     * @param reducer Optional Reducer that will be used for State reducing, see [MviReducer].
     * If not provided, then State will never change.
     * @param State type of Store's State
     * @param Intent type of Store's Intents
     * @param Label type of Store's Labels
     * @param Action type of Store's Actions
     * @param Result type of Store's Results
     * @return a new instance of Store
     */
    @MainThread
    fun <State : Any, Intent : Any, Label : Any, Action : Any, Result : Any> create(
        name: String,
        initialState: State,
        bootstrapper: MviBootstrapper<Action>? = null,
        intentToAction: (Intent) -> Action,
        executorFactory: () -> MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result> = getBypassReducer()
    ): MviStore<State, Intent, Label>

    companion object {
        private val bypassReducer =
            object : MviReducer<Nothing, Any> {
                override fun Nothing.reduce(result: Any): Nothing = this
            }

        @Suppress("UNCHECKED_CAST")
        internal fun <State : Any, Result : Any> getBypassReducer(): MviReducer<State, Result> = bypassReducer as MviReducer<State, Result>
    }
}
