package com.arkivanov.mvikotlin.keepers.statekeeper

/**
 * Provides a way to save and restore state (e.g. a `Store`'s state).
 * A typical use case is Android Activity recreation due to system constraints.
 */
@ExperimentalStateKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
interface StateKeeper<T : Any> {

    /**
     * Returns a previously saved state, if any
     */
    fun consume(): T?

    /**
     * Registers a state supplier
     *
     * @param supplier a state supplier that will be called when it's time to save the state
     */
    fun register(supplier: () -> T)

    /**
     * Unregisters (removes) a previously registered state supplier
     */
    fun unregister()
}
