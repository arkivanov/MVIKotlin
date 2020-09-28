package com.arkivanov.mvikotlin.core.instancekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceKeeper as OldInstanceKeeper

/**
 * Either returns a currently retained instance or creates (and retains) a new one.
 *
 * See [InstanceKeeper] for more information.
 *
 * @param factory a factory function, accepts the [InstanceKeeper]'s [Lifecycle],
 * called when there is no retained instance yet
 * @return either a currently retained instance or a new one
 */
@ExperimentalInstanceKeeperApi
inline fun <T : Any> OldInstanceKeeper<T>.getOrCreate(factory: (Lifecycle) -> T): T {
    check(lifecycle.state != Lifecycle.State.DESTROYED) { "The InstanceKeeper is already destroyed" }

    return instance ?: factory(lifecycle).also { instance = it }
}

/**
 * Either returns a currently retained [Store] instance or creates (and retains) a new one.
 * The retained [Store] is automatically disposed at the end of the [InstanceKeeper]'s lifecycle.
 *
 * See [InstanceKeeper] for more information.
 *
 * @param factory a factory function, called when there is no retained instance yet
 * @return either a currently retained [Store] instance or a new one
 */
@ExperimentalInstanceKeeperApi
fun <T : Store<*, *, *>> OldInstanceKeeper<T>.getOrCreateStore(factory: () -> T): T =
    getOrCreate { lifecycle ->
        val store = factory()
        lifecycle.doOnDestroy(store::dispose)
        store
    }

@com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
fun <T : Store<*, *, *>> InstanceKeeper.getOrCreateStore(key: Any, factory: () -> T): T =
    getOrCreate(key) {
        StoreInstance(factory())
    }.store

@com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
inline fun <reified T : Store<*, *, *>> InstanceKeeper.getOrCreateStore(
    noinline factory: () -> T
): T =
    getOrCreateStore(T::class, factory)

@com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
private class StoreInstance<out T : Store<*, *, *>>(
    val store: T
) : InstanceKeeper.Instance {
    override fun onDestroy() {
        store.dispose()
    }
}
