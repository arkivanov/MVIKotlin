package com.arkivanov.mvidroid.store.defaultstore

import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.MviStoreFactory
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer

/**
 * Default implementation of [MviStoreFactory] that creates default implementation of Store
 */
object MviDefaultStoreFactory : MviStoreFactory {

    override fun <State : Any, Intent : Any, Label : Any, Action : Any, Result : Any> create(
        name: String,
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        intentToAction: (Intent) -> Action,
        executorFactory: () -> MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>
    ): MviStore<State, Intent, Label> = MviDefaultStore(initialState, bootstrapper, intentToAction, executorFactory(), reducer)
}
