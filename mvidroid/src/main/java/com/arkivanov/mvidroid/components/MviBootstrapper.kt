package com.arkivanov.mvidroid.components

import android.support.annotation.MainThread
import com.arkivanov.kfunction.KConsumer
import com.arkivanov.mvidroid.store.MviAction
import io.reactivex.disposables.Disposable

/**
 * Used for Store bootstrapping. Subscribe to data sources or do any other initialization.
 *
 * @param A type of Action
 */
interface MviBootstrapper<out A : MviAction<*, *, *>> {

    /**
     * Bootstraps a Store, invoked by Store always on Main thread
     *
     * @param dispatch a consumer of Actions
     * @return Disposable if there are any background operations, null otherwise.
     * It will be disposed together with Store.
     */
    @MainThread
    fun bootstrap(dispatch: KConsumer<A>): Disposable?
}
