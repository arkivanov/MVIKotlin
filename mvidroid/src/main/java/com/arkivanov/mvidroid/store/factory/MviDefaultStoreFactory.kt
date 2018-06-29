package com.arkivanov.mvidroid.store.factory

import com.arkivanov.kfunction.KConsumer
import com.arkivanov.kfunction.KSupplier
import com.arkivanov.mvidroid.components.MviAction
import com.arkivanov.mvidroid.components.MviBootstrapper
import com.arkivanov.mvidroid.components.MviIntentToAction
import com.arkivanov.mvidroid.components.MviReducer
import com.arkivanov.mvidroid.store.MviDefaultStore
import com.arkivanov.mvidroid.store.MviStore
import io.reactivex.disposables.Disposable

/**
 * Default implementation of [MviStoreFactory], creates default implementation of Store
 */
object MviDefaultStoreFactory : MviStoreFactory {

    private val BYPASS_REDUCER =
        object : MviReducer<Nothing, Any> {
            override fun Nothing.reduce(result: Any): Nothing = this
        }

    private val NO_OP_INTENT_TO_ACTION =
        object : MviIntentToAction<Nothing, MviAction<*, *, *>> {
            override fun select(intent: Nothing): MviAction<*, *, *> {
                throw UnsupportedOperationException("WTF?")
            }
        }

    private val BYPASS_INTENT_TO_ACTION =
        object : MviIntentToAction<Any, MviAction<*, Any, *>> {
            override fun select(intent: Any): MviAction<*, Any, *> =
                object : MviAction<Any, Any, Any> {
                    override fun invoke(getState: KSupplier<Any>, dispatch: KConsumer<Any>, publish: KConsumer<Any>): Disposable? {
                        dispatch(intent)
                        return null
                    }
                }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <State : Any, Result : Any> getBypassReducer(): MviReducer<State, Result> = BYPASS_REDUCER as MviReducer<State, Result>

    @Suppress("UNCHECKED_CAST")
    private fun <Action : MviAction<*, *, *>> getNoOpIntentToAction(): MviIntentToAction<Nothing, Action> =
        NO_OP_INTENT_TO_ACTION as MviIntentToAction<Nothing, Action>

    @Suppress("UNCHECKED_CAST")
    private fun <Intent : Any, Action : MviAction<*, *, *>> getBypassIntentToAction(): MviIntentToAction<Intent, Action> =
        BYPASS_INTENT_TO_ACTION as MviIntentToAction<Intent, Action>

    override fun <State : Any, Intent : Any, Result : Any, Label : Any, Action : MviAction<State, Result, Label>> create(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        intentToAction: MviIntentToAction<Intent, Action>,
        reducer: MviReducer<State, Result>?
    ): MviStore<State, Intent, Label> =
        MviDefaultStore(
            initialState,
            bootstrapper,
            intentToAction,
            reducer ?: getBypassReducer()
        )

    override fun <State : Any, Result : Any, Label : Any, Action : MviAction<State, Result, Label>> createIntentless(
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        reducer: MviReducer<State, Result>?
    ): MviStore<State, Nothing, Label> =
        create(
            initialState = initialState,
            bootstrapper = bootstrapper,
            intentToAction = getNoOpIntentToAction(),
            reducer = reducer
        )

    override fun <State : Any, Intent : Any, Label : Any> createActionless(
        initialState: State,
        reducer: MviReducer<State, Intent>?
    ): MviStore<State, Intent, Label> =
        create(
            initialState = initialState,
            intentToAction = getBypassIntentToAction(),
            reducer = reducer
        )
}
