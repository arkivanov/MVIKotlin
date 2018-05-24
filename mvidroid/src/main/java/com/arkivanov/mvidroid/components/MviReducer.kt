package com.arkivanov.mvidroid.components

import android.support.annotation.MainThread

/**
 * Applies Results to States
 *
 * @param S type of State
 * @param R type of Result
 */
interface MviReducer<S : Any, in R : Any> {

    /**
     * Invoked by Store for every Result, always on Main thread.
     * This is an extension function of State so current State is available as this.
     *
     * @param result a Result that should be applied to State
     * @return new State with Result applied
     */
    @MainThread
    fun S.reduce(result: R): S
}
