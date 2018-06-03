package com.arkivanov.mvidroid.store

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.components.MviAction
import com.arkivanov.mvidroid.components.MviBootstrapper
import com.arkivanov.mvidroid.components.MviIntentToAction
import com.arkivanov.mvidroid.components.MviReducer

/**
 * Store Factory can be used to create Store implementations dynamically. See example in [MviStore].
 * You can create different Store wrappers and combine them depending on circumstances.
 */
interface MviStoreFactory {

    /**
     * Creates an implementation of Store
     *
     * @param initialState initial State of Store
     * @param bootstrapper Bootstrapper for Store initialization, see [MviBootstrapper]
     * @param intentToAction A function that maps Intents to Actions, see [MviIntentToAction]
     * @param reducer Reducer that will be used for State reducing, see [MviReducer]
     * @param S type of Store's State
     * @param I type of Store's Intents
     * @param R type of Store's Results
     * @param L type of Store's Labels
     * @param A type of Store's Actions
     * @return a new instance of Store
     */
    @MainThread
    operator fun <S : Any, I : Any, R : Any, L : Any, A : MviAction<S, R, L>> invoke(
        initialState: S,
        bootstrapper: MviBootstrapper<A>? = null,
        intentToAction: MviIntentToAction<I, A>,
        reducer: MviReducer<S, R>
    ): MviStore<S, I, L>
}
