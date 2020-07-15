package com.arkivanov.mvikotlin.core.statekeeper

/**
 * Represents a controller for state preservation, responsible for manging state suppliers and consumers.
 *
 * @param C a type of the state container
 * @param T a type of the state
 */
interface StateKeeperController<in C : Any, in T : Any> : StateKeeperProvider<T> {

    /**
     * Calls all registered state suppliers and saves their states into the provided container
     *
     * @param container a storage for state preservation
     */
    fun save(container: C)
}
