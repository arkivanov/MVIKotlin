package com.arkivanov.mvidroid.components

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.MviAction

/**
 * Converts Intents to Actions
 *
 * @param I type of Intent
 * @param A type of Action
 */
interface MviIntentToAction<in I : Any, out A : MviAction<*, *>> {

    /**
     * Invoked by Store for every Intent, always on Main thread
     *
     * @param intent an Intent
     * @return Corresponding Action for Intent
     */
    @MainThread
    fun select(intent: I): A
}
