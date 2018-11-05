package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.MviStoreFactory
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer

/**
 * An implementation of [MviStoreFactory] that creates Stores with time travel functionality.
 *
 * See [MviTimeTravelController], [MviTimeTravelView][com.arkivanov.mvidroid.widget.MviTimeTravelView]
 * and [MviTimeTravelDrawer][com.arkivanov.mvidroid.widget.MviTimeTravelDrawer] for more information.
 */
object MviTimeTravelStoreFactory : MviStoreFactory {

    override fun <State : Any, Intent : Any, Action : Any, Result : Any, Label : Any> create(
        name: String,
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        intentToAction: (Intent) -> Action,
        executorFactory: () -> MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>
    ): MviStore<State, Intent, Label> =
        MviTimeTravelStore(
            name = name,
            initialState = initialState,
            bootstrapper = bootstrapper,
            intentToAction = intentToAction,
            executorFactory = executorFactory,
            reducer = reducer
        )
            .also { MviTimeTravelController.attachStore(it, name) }
}