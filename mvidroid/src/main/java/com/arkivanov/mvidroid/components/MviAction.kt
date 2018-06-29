package com.arkivanov.mvidroid.components

import android.support.annotation.MainThread
import com.arkivanov.kfunction.KConsumer
import com.arkivanov.kfunction.KSupplier
import io.reactivex.disposables.Disposable

/**
 * Actions contain business logic of Store
 *
 * @param State type of Store's State
 * @param Result type of Store's Results
 * @param Label type of Store's Labels
 */
interface MviAction<in State : Any, out Result : Any, out Label : Any> {

    /**
     * Do your job here, called on Main thread
     *
     * @param getState provides current state of Store, must by called only on Main thread
     * @param dispatch Synchronously dispatches a Result to Reducer, must by called only on Main thread.
     * New State will be available right after this function return.
     * @param publish synchronously publishes a Label, must by called only on Main thread
     * @return Disposable if there are any background operations, null otherwise. This Disposable will be managed by Store.
     */
    @MainThread
    operator fun invoke(getState: KSupplier<State>, dispatch: KConsumer<Result>, publish: KConsumer<Label>): Disposable?
}
