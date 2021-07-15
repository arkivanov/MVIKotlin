package com.arkivanov.mvikotlin.core.instancekeeper

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.keepers.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper

@ExperimentalInstanceKeeperApi
fun <T : Store<*, *, *>> InstanceKeeper.getStore(key: Any, factory: () -> T): T =
    get(key) {
        StoreInstance(factory())
    }.store

@ExperimentalInstanceKeeperApi
inline fun <reified T : Store<*, *, *>> InstanceKeeper.getStore(noinline factory: () -> T): T = getStore(T::class, factory)

@ExperimentalInstanceKeeperApi
private class StoreInstance<out T : Store<*, *, *>>(
    val store: T
) : InstanceKeeper.Instance {
    override fun onDestroy() {
        store.dispose()
    }
}
