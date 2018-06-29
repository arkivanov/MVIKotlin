package com.arkivanov.mvidroid.store.factory

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.components.MviAction
import com.arkivanov.mvidroid.components.MviBootstrapper
import com.arkivanov.mvidroid.components.MviIntentToAction
import com.arkivanov.mvidroid.components.MviReducer
import com.arkivanov.mvidroid.store.MviStore

/**
 * Store Factory can be used to create Store implementations dynamically. See example in [MviStore].
 * You can create different Store wrappers and combine them depending on circumstances.
 */
interface MviStoreFactory {

    /**
     * Creates an implementation of Store, must be called on Main thread
     *
     * @param initialState initial State of Store
     * @param bootstrapper optional Bootstrapper for Store initialization, see [MviBootstrapper]
     * @param intentToAction Action function that maps Intents to Actions, see [MviIntentToAction]
     * @param reducer Optional Reducer that will be used for State reducing, see [MviReducer].
     * If not provided, then State will never change.
     * @param State type of Store's State
     * @param Intent type of Store's Intents
     * @param Result type of Store's Results
     * @param Label type of Store's Labels
     * @param Action type of Store's Actions
     * @return a new instance of Store
     */
    @MainThread
    fun <State : Any, Intent : Any, Result : Any, Label : Any, Action : MviAction<State, Result, Label>> create(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>? = null,
        intentToAction: MviIntentToAction<Intent, Action>,
        reducer: MviReducer<State, Result>? = null
    ): MviStore<State, Intent, Label>

    /**
     * Creates an implementation of Store that does not accept Intents
     * (e.g. Store that is initialized by Bootstrapper and works by itself).
     * Must be called on Main thread.
     *
     * @param initialState initial State of Store
     * @param bootstrapper optional Bootstrapper for Store initialization, see [MviBootstrapper]
     * @param reducer Optional Reducer that will be used for State reducing, see [MviReducer].
     * If not provided, then State will never change.
     * @param State type of Store's State
     * @param Result type of Store's Results
     * @param Label type of Store's Labels
     * @param Action type of Store's Actions
     * @return a new instance of Store
     */
    @MainThread
    fun <State : Any, Result : Any, Label : Any, Action : MviAction<State, Result, Label>> createIntentless(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>? = null,
        reducer: MviReducer<State, Result>? = null
    ): MviStore<State, Nothing, Label>

    /**
     * Creates an implementation of Store that does not have Actions and Results. Any Intent is passed directly to Reducer.
     * Must be called on Main thread.
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
    fun <State : Any, Intent : Any, Label : Any> createActionless(
        initialState: State,
        reducer: MviReducer<State, Intent>? = null
    ): MviStore<State, Intent, Label>
}
