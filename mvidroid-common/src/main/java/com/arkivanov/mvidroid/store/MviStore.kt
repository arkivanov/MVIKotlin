package com.arkivanov.mvidroid.store

import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Base interface of Store. Store is a place for business logic, it consumes Intents and produces States.
 * It also can produce Labels as side effects. Use [MviComponent][com.arkivanov.mvidroid.component.MviAbstractComponent]
 * to group your Stores into UI independent components. See [MviDefaultStore] for more information.
 *
 * @param State Type of State. State is a storage of Store's data, typically represented as data object.
 * @param Intent Type of Intent. Intent is a call to action, it triggers some Action in Store.
 * @param Label Type of Label. Labels are used for inter-Store communication.
 */
interface MviStore<out State : Any, in Intent : Any, out Label : Any> : Disposable {

    /**
     * Provides access to current state, must be accessed only from Main thread
     */
    @get:MainThread
    val state: State

    /**
     * Observable of States. Emissions are performed on Main thread.
     */
    val states: Observable<out State>

    /**
     * Observable of Labels. Emissions are performed on Main thread.
     */
    val labels: Observable<out Label>

    /**
     * Sends Intent to Store, must me called only on Main thread
     */
    @MainThread
    fun accept(intent: Intent)

    /**
     * Disposes the Store and all its active operations, must be called only on Main thread
     */
    @MainThread
    override fun dispose()

    /**
     * Checks whether Store is disposed or not, must be called only on Main thread
     *
     * @return true if Store is disposed, false otherwise
     */
    @MainThread
    override fun isDisposed(): Boolean
}
