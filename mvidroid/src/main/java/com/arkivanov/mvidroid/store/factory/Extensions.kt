package com.arkivanov.mvidroid.store.factory

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import io.reactivex.disposables.Disposable

private val noOpIntentToAction: (Nothing) -> Any = { throw UnsupportedOperationException("WTF?") }

private val bypassIntentToAction: (Any) -> Any = { it }

private val bypassExecutorFactory: () -> MviExecutor<Any, Any, Any, Any> =
    {
        object : MviExecutor<Any, Any, Any, Any>() {
            override fun execute(action: Any): Disposable? {
                dispatch(action)
                return null
            }
        }
    }

/**
 * Creates an implementation of Store that does not accept Intents
 * (e.g. Store that is initialized by Bootstrapper and works by itself).
 * Must be called only on Main thread.
 * See [MviStoreFactory.create] for more information.
 */
@MainThread
fun <State : Any, Action : Any, Result : Any, Label : Any> MviStoreFactory.createIntentless(
    name: String,
    initialState: State,
    bootstrapper: MviBootstrapper<Action>?,
    executorFactory: () -> MviExecutor<State, Action, Result, Label>,
    reducer: MviReducer<State, Result> = MviStoreFactory.getBypassReducer()
): MviStore<State, Nothing, Label> = create(name, initialState, bootstrapper, getNoOpIntentToAction(), executorFactory, reducer)

/**
 * Creates an implementation of Store that does not have Actions.
 * Executor uses Intents directly. Must be called on Main thread.
 * See [MviStoreFactory.create] for more information.
 */
@MainThread
fun <State : Any, Intent : Any, Result : Any, Label : Any> MviStoreFactory.createActionless(
    name: String,
    initialState: State,
    executorFactory: () -> MviExecutor<State, Intent, Result, Label>,
    reducer: MviReducer<State, Result> = MviStoreFactory.getBypassReducer()
): MviStore<State, Intent, Label> = create(name, initialState, null, getBypassIntentToAction(), executorFactory, reducer)

/**
 * Creates an implementation of Store that does not have Executor as well as Actions and Results.
 * Intents are passed directly to Reducer. Must be called on Main thread.
 * See [MviStoreFactory.create] for more information.
 */
@MainThread
fun <State : Any, Intent : Any, Label : Any> MviStoreFactory.createExecutorless(
    name: String,
    initialState: State,
    reducer: MviReducer<State, Intent>
): MviStore<State, Intent, Label> =
    create<State, Intent, Intent, Intent, Label>(
        name,
        initialState,
        null,
        getBypassIntentToAction(),
        getBypassExecutorFactory(),
        reducer
    )

@Suppress("UNCHECKED_CAST")
private fun <Action : Any> getNoOpIntentToAction(): (Nothing) -> Action = noOpIntentToAction as (Nothing) -> Action

@Suppress("UNCHECKED_CAST")
private fun <Intent : Any> getBypassIntentToAction(): (Intent) -> Intent = bypassIntentToAction as (Intent) -> Intent

@Suppress("UNCHECKED_CAST")
private fun <State : Any, Intent : Any, Label : Any> getBypassExecutorFactory(): () -> MviExecutor<State, Intent, Intent, Label> =
    bypassExecutorFactory as () -> MviExecutor<State, Intent, Intent, Label>
