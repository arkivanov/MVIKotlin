package com.arkivanov.mvikotlin.core.statekeeper

import com.badoo.reaktive.utils.ensureNeverFrozen

internal class StateKeeperContainerImpl<in State : Any, in T : Any>(
    private val get: (state: State, key: String) -> T?,
    private val put: (state: State, key: String, value: T) -> Unit
) : StateKeeperContainer<State, T> {

    init {
        ensureNeverFrozen()
    }

    private val suppliers = HashMap<String, () -> T>()

    override fun getProvider(savedState: State?): StateKeeperProvider<T> =
        StateKeeperProviderImpl(
            savedState = savedState,
            get = get,
            suppliers = suppliers
        )

    override fun save(outState: State) {
        suppliers.forEach { (key, supplier) ->
            put(outState, key, supplier())
        }
    }
}
