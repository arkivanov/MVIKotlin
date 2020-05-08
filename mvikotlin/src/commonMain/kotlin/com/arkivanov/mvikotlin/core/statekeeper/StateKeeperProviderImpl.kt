package com.arkivanov.mvikotlin.core.statekeeper

internal class StateKeeperProviderImpl<State : Any, in T : Any>(
    private val savedState: State?,
    private val get: (state: State, key: String) -> T?,
    private val suppliers: MutableMap<String, () -> T>
) : StateKeeperProvider<T> {

    override fun <S : T> get(key: String): StateKeeper<S> =
        object : StateKeeper<S> {
            @Suppress("UNCHECKED_CAST")
            override val state: S?
                get() = savedState?.let { get(it, key) } as S?

            override fun register(supplier: () -> S) {
                suppliers[key] = supplier
            }
        }
}
