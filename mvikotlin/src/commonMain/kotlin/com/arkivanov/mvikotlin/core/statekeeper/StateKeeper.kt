package com.arkivanov.mvikotlin.core.statekeeper

/**
 * Provides a way to save and restore state (e.g. a `Store`'s state).
 * A typical use case is Android Activity recreation due to system constraints.
 */
interface StateKeeper<T : Any> {

    /**
     * Returns a previously saved state, if any
     */
    fun getState(): T?

    /**
     * Sets or removes a state supplier
     *
     * @param supplier a state supplier that will be called when it's time to save the state, or `null` to remove the supplier
     */
    fun setSupplier(supplier: (() -> T)?)
}
