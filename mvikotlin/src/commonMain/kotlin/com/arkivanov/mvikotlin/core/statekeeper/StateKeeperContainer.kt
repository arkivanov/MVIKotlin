package com.arkivanov.mvikotlin.core.statekeeper

/**
 * Represents a container for [StateKeeper]s
 */
interface StateKeeperContainer<in State : Any, in T : Any> {

    /**
     * Provides instances of [StateKeeperProvider]
     *
     * @param savedState a previously saved state
     * @return an instance of [StateKeeperProvider]
     */
    fun getProvider(savedState: State?): StateKeeperProvider<T>

    /**
     * Calls all registered state suppliers and saves the states into provided storage
     *
     * @param outState a storage to save
     */
    fun save(outState: State)
}
