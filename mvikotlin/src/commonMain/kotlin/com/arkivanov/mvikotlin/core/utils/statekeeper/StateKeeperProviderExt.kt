package com.arkivanov.mvikotlin.core.utils.statekeeper

inline fun <T : Any, reified S : T> StateKeeperProvider<T>.get(): StateKeeper<S> =
    get(S::class.toString())
