package com.arkivanov.mvidroid.store.component

import android.support.annotation.MainThread

/**
 * Applies Results to States
 *
 * @param State type of State
 * @param Result type of Result
 */
interface MviReducer<State : Any, in Result : Any> {

    /**
     * Called by Store for every Result, always on Main thread.
     * This is an extension function of State so current State is available as this.
     *
     * @param result a Result that should be applied to State
     * @return new State with Result applied
     */
    @MainThread
    fun State.reduce(result: Result): State
}
