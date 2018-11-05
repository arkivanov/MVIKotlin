package com.arkivanov.mvidroid.component

import android.support.annotation.MainThread
import io.reactivex.disposables.Disposable

/**
 * Represents a group of Stores.
 *
 * Responsibilities:
 * * Transforms Events to Intents and redirects them to appropriate Stores.
 * * Provides a group of Stores' States
 * * Takes care of disposing of its Stores, only non-persistent Stores are disposed.
 *
 * @param Event type of Events
 * @param States type of States, typically includes States from all Component's Stores
 */
interface MviComponent<in Event : Any, out States : Any> : Disposable {

    /**
     * A group of States of Component's Stores. Must be accessed only from Main thread.
     */
    @get:MainThread
    val states: States

    /**
     * Use this method to send a UI Events to Component.
     * Every UI Event will be converted to Stores' Intents and redirected to appropriate Stores.
     * Must be called only on Main thread.
     */
    @MainThread
    fun accept(event: Event)

    /**
     * Disposed Component and all its non-persistent Stores. Must be called only on Main thread.
     */
    @MainThread
    override fun dispose()

    /**
     * Checks whether this Component is disposed or not. Must be called only on Main thread.
     *
     * @return true if Component is disposed, false otherwise
     */
    @MainThread
    override fun isDisposed(): Boolean
}
