package com.arkivanov.mvikotlin.core.statekeeper

/**
 * A convenience method for [StateKeeperProvider.get]
 */
inline fun <T : Any, reified S : T> StateKeeperProvider<T>.get(key: String = S::class.toString()): StateKeeper<S> =
    get(S::class, key)
