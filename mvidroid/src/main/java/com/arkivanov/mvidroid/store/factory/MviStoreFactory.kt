package com.arkivanov.mvidroid.store.factory

import android.support.annotation.MainThread
import com.arkivanov.kfunction.KFunction
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer

/**
 * Store Factory can be used to create Store implementations dynamically. See example in [MviStore].
 * You can create different Store wrappers and combine them depending on circumstances.
 */
interface MviStoreFactory {

    /**
     * Creates an implementation of Store, must be called only on Main thread
     *
     * @param initialState initial State of Store
     * @param bootstrapper optional Bootstrapper for Store initialization, see [MviBootstrapper]
     * @param intentToAction a function that maps Intents to Actions
     * @param executor an Executor that will execute Store's Actions, see [MviExecutor]
     * @param reducer Optional Reducer that will be used for State reducing, see [MviReducer].
     * If not provided, then State will never change.
     * @param State type of Store's State
     * @param Intent type of Store's Intents
     * @param Action type of Store's Actions
     * @param Result type of Store's Results
     * @param Label type of Store's Labels
     * @return a new instance of Store
     */
    @MainThread
    fun <State : Any, Intent : Any, Action : Any, Result : Any, Label : Any> create(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>? = null,
        intentToAction: KFunction<Intent, Action>,
        executor: MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>? = null
    ): MviStore<State, Intent, Label>

    /**
     * Creates an implementation of Store that does not accept Intents
     * (e.g. Store that is initialized by Bootstrapper and works by itself).
     * Must be called only on Main thread.
     *
     * @param initialState initial State of Store
     * @param bootstrapper optional Bootstrapper for Store initialization, see [MviBootstrapper]
     * @param executor an Executor that will execute Store's Actions, see [MviExecutor]
     * @param reducer Optional Reducer that will be used for State reducing, see [MviReducer].
     * If not provided, then State will never change.
     * @param State type of Store's State
     * @param Action type of Store's Actions
     * @param Result type of Store's Results
     * @param Label type of Store's Labels
     * @return a new instance of Store
     */
    @MainThread
    fun <State : Any, Action : Any, Result : Any, Label : Any> createIntentless(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>? = null,
        executor: MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>? = null
    ): MviStore<State, Nothing, Label>

    /**
     * Creates an implementation of Store that does not have Actions.
     * Executor uses Intents directly. Must be called on Main thread.
     *
     * @param initialState initial State of Store
     * @param executor an Executor that will accept Store's Intents, see [MviExecutor]
     * @param reducer Optional Reducer that will be used for State reducing, see [MviReducer].
     * If not provided, then State will never change.
     * @param State type of Store's State
     * @param Intent type of Store's Intents
     * @param Result type of Store's Results
     * @param Label type of Store's Labels
     * @return a new instance of Store
     */
    @MainThread
    fun <State : Any, Intent : Any, Result : Any, Label : Any> createActionless(
        initialState: State,
        executor: MviExecutor<State, Intent, Result, Label>,
        reducer: MviReducer<State, Result>? = null
    ): MviStore<State, Intent, Label>

    /**
     * Creates an implementation of Store that does not have Executor as well as Actions and Results.
     * Intents are passed directly to Reducer. Must be called on Main thread.
     *
     * @param initialState initial State of Store
     * @param reducer Optional Reducer that will be used for State reducing, see [MviReducer].
     * If not provided, then State will never change.
     * @param State type of Store's State
     * @param Intent type of Store's Intents
     * @param Label type of Store's Labels
     * @return a new instance of Store
     */
    @MainThread
    fun <State : Any, Intent : Any, Label : Any> createExecutorless(
        initialState: State,
        reducer: MviReducer<State, Intent>? = null
    ): MviStore<State, Intent, Label>
}
