package com.arkivanov.mvidroid.components

import android.support.annotation.MainThread

/**
 * Converts Intents to Actions
 *
 * @param Intent type of Intent
 * @param Action type of Action
 */
interface MviIntentToAction<in Intent : Any, out Action : MviAction<*, *, *>> {

    /**
     * Called by Store for every Intent, always on Main thread
     *
     * @param intent an Intent
     * @return Corresponding Action for Intent
     */
    @MainThread
    fun select(intent: Intent): Action
}
