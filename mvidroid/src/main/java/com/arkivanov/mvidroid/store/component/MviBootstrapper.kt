package com.arkivanov.mvidroid.store.component

import android.support.annotation.MainThread
import com.arkivanov.kfunction.KConsumer
import io.reactivex.disposables.Disposable

/**
 * Used for Store bootstrapping. Dispatch initial Actions, subscribe to data sources or do any other initializations.
 *
 * @param Action type of Action
 */
interface MviBootstrapper<out Action : Any> {

    /**
     * Bootstraps a Store, called by Store always on Main thread
     *
     * @param dispatch a consumer of Actions, must be invoked only on Main thread
     * @return Disposable if there are any background operations, null otherwise.
     * This Disposable will be managed by Store.
     */
    @MainThread
    fun bootstrap(dispatch: KConsumer<Action>): Disposable?
}
