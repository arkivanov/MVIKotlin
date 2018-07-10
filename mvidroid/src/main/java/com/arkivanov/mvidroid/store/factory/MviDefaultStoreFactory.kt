package com.arkivanov.mvidroid.store.factory

import com.arkivanov.mvidroid.store.MviDefaultStore
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import io.reactivex.disposables.Disposable

/**
 * Default implementation of [MviStoreFactory] that creates default implementation of Store
 */
object MviDefaultStoreFactory : MviStoreFactory {

    private val BYPASS_REDUCER =
        object : MviReducer<Nothing, Any> {
            override fun Nothing.reduce(result: Any): Nothing = this
        }

    private val NO_OP_INTENT_TO_ACTION: (Nothing) -> Any = { throw UnsupportedOperationException("WTF?") }

    private val BYPASS_INTENT_TO_ACTION: (Any) -> Any = { it }

    private val BYPASS_EXECUTOR =
        object : MviExecutor<Any, Any, Any, Any>() {
            override fun invoke(action: Any): Disposable? {
                dispatch(action)
                return null
            }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <State : Any, Result : Any> getBypassReducer(): MviReducer<State, Result> = BYPASS_REDUCER as MviReducer<State, Result>

    @Suppress("UNCHECKED_CAST")
    private fun <Action : Any> getNoOpIntentToAction(): (Nothing) -> Action = NO_OP_INTENT_TO_ACTION as (Nothing) -> Action

    @Suppress("UNCHECKED_CAST")
    private fun <Intent : Any> getBypassIntentToAction(): (Intent) -> Intent = BYPASS_INTENT_TO_ACTION as (Intent) -> Intent

    @Suppress("UNCHECKED_CAST")
    private fun <State : Any, Intent : Any, Label : Any> getBypassExecutor(): MviExecutor<State, Intent, Intent, Label> =
        BYPASS_EXECUTOR as MviExecutor<State, Intent, Intent, Label>

    override fun <State : Any, Intent : Any, Action : Any, Result : Any, Label : Any> create(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        intentToAction: (Intent) -> Action,
        executor: MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>?
    ): MviStore<State, Intent, Label> =
        MviDefaultStore(initialState, bootstrapper, intentToAction, executor, reducer ?: getBypassReducer())

    override fun <State : Any, Action : Any, Result : Any, Label : Any> createIntentless(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        executor: MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>?
    ): MviStore<State, Nothing, Label> = create(initialState, bootstrapper, getNoOpIntentToAction(), executor, reducer)

    override fun <State : Any, Intent : Any, Result : Any, Label : Any> createActionless(
        initialState: State,
        executor: MviExecutor<State, Intent, Result, Label>,
        reducer: MviReducer<State, Result>?
    ): MviStore<State, Intent, Label> =
        create(initialState, null, getBypassIntentToAction(), executor, reducer)

    override fun <State : Any, Intent : Any, Label : Any> createExecutorless(
        initialState: State,
        reducer: MviReducer<State, Intent>?
    ): MviStore<State, Intent, Label> =
        create<State, Intent, Intent, Intent, Label>(initialState, null, getBypassIntentToAction(), getBypassExecutor(), reducer)
}
