package com.arkivanov.mvidroid.store

import android.support.annotation.MainThread
import com.arkivanov.kfunction.KConsumer
import com.arkivanov.kfunction.KSupplier
import io.reactivex.disposables.Disposable

/**
 * Actions contain business logic of Store
 *
 * @param S type of Store's State
 * @param R type of Store's Results
 */
interface MviAction<in S : Any, out R : Any> {

    /**
     * Called on Main thread, do your job here
     *
     * @param getState provides current state of Store, must by called only on Main thread
     * @param dispatch Synchronously dispatches a Result to Reducer, must by called only on Main thread.
     * New State will be available right after this function return.
     * @param publish synchronously publishes a Label, must by called only on Main thread
     * @return Disposable if there are any background operations, null otherwise. This Disposable will be managed by Store.
     */
    @MainThread
    operator fun invoke(getState: KSupplier<S>, dispatch: KConsumer<R>, publish: KConsumer<Any>): Disposable?
}
