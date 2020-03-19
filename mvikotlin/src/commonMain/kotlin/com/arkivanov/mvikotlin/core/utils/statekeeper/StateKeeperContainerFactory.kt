package com.arkivanov.mvikotlin.core.utils.statekeeper

/**
 * Creates a new instance of [StateKeeperContainer]
 *
 * @param get a function that returns a value from a state by key
 * @param put a function that puts a value into a state by key
 *
 */
@Suppress("FunctionName") // Factory function
fun <State : Any, T : Any> StateKeeperContainer(
    get: (state: State, key: String) -> T?,
    put: (state: State, key: String, value: T) -> Unit
): StateKeeperContainer<State, T> =
    StateKeeperContainerImpl(get = get, put = put)
