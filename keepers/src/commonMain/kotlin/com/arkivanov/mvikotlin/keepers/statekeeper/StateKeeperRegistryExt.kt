package com.arkivanov.mvikotlin.keepers.statekeeper

/**
 * A convenience method for [StateKeeperRegistry.get]
 */
@ExperimentalStateKeeperApi
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
inline fun <T : Any, reified S : T> StateKeeperRegistry<T>.get(key: String = S::class.toString()): StateKeeper<S> =
    get(S::class, key)
