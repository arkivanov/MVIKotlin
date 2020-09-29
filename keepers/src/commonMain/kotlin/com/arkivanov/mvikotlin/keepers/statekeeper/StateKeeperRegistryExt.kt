package com.arkivanov.mvikotlin.keepers.statekeeper

/**
 * A convenience method for [StateKeeperRegistry.get]
 */
@ExperimentalStateKeeperApi
inline fun <T : Any, reified S : T> StateKeeperRegistry<T>.get(key: String = S::class.toString()): StateKeeper<S> =
    get(S::class, key)
