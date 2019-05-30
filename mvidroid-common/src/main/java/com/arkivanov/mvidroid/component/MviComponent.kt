package com.arkivanov.mvidroid.component

import android.support.annotation.MainThread
import io.reactivex.disposables.Disposable

/**
 * [MviComponent] is deprecated in favour of manual bindings with RxUtils.kt from com.arkivanov.mvidroid.utils package.
 * Bind all your sources and consumers directly in Activity/Fragment or delegate to some sort of custom component.
 *
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
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Use RxUtils.kt from com.arkivanov.mvidroid.utils package and bind sources to consumers manually")
interface MviComponent<in Event : Any, out States : Any> : Disposable {

    /**
     * A group of States of Component's Stores. Must be accessed only from Main thread.
     */
    @get:MainThread
    val states: States

    /**
     * Use this method to send Events to Component.
     * Every Event will be converted to Intents that will be redirected to appropriate Stores.
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
