package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.badoo.reaktive.disposable.scope.DisposableScope

/**
 * Allows DSL [Bootstrapper][com.arkivanov.mvikotlin.core.store.Bootstrapper]s to launch asynchronous tasks
 * and [dispatch] ``[Action]s.
 *
 * Implements [DisposableScope] that is disposed when the [Bootstrapper][com.arkivanov.mvikotlin.core.store.Bootstrapper] is disposed.
 *
 * @see reaktiveBootstrapper
 */
@ExperimentalMviKotlinApi
interface ReaktiveBootstrapperScope<in Action : Any> : DisposableScope {

    /**
     * Dispatches the [Action] to the [Store][com.arkivanov.mvikotlin.core.store.Store].
     *
     * @param action an [Action] to be dispatched.
     */
    fun dispatch(action: Action)
}
