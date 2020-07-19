package com.arkivanov.mvikotlin.core.statekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.store.Store

/**
 * A convenience method for [StateKeeperProvider.get]
 */
inline fun <T : Any, reified S : T> StateKeeperProvider<T>.get(key: String = S::class.toString()): StateKeeper<S> =
    get(S::class, key)

/**
 * Provides a way to retain instances via [StateKeeper].
 * A typical use case is to retain instances on Android configuration change.
 *
 * The initial `instance` is created using the provided `factory` function that accepts a special [Lifecycle].
 * The special [Lifecycle] is same as the original one
 * but its [onDestroy][Lifecycle.Callbacks.onDestroy] method is not called when the `instance` is retained.
 * The `instance` is retained and restored together with its special [Lifecycle].
 * After restoration the special [Lifecycle]'s [onCreate][Lifecycle.Callbacks.onCreate] method is also not called,
 * since it was not destroyed. The special [Lifecycle]'s [onDestroy][Lifecycle.Callbacks.onDestroy] method is called
 * when the original [Lifecycle] is destroyed and the `instance` was not retained.
 *
 * @param lifecycle an original [Lifecycle] to be used
 * @param key a key that should be used for `instance` preservation, see [StateKeeperProvider.get] for more information
 * @param factory a factory function that accepts the special [Lifecycle] and returns an `instance` to be retained later
 * @return either a retained `instance` or a new `instance` created by the `factory` function if there is no retained one
 */
fun <T : Any> StateKeeperProvider<Any>?.retainInstance(lifecycle: Lifecycle, key: String, factory: (Lifecycle) -> T): T {
    if (this == null) {
        return factory(lifecycle)
    }

    val stateKeeper: StateKeeper<RetainedInstance<T>> = get(key)

    val retainedInstance: RetainedInstance<T>? = stateKeeper.getState()
    val lifecycleRegistry = retainedInstance?.lifecycleRegistry ?: LifecycleRegistry()
    val instance = retainedInstance?.instance ?: factory(lifecycleRegistry)

    var isStateSaved = false

    val stateSupplier: () -> RetainedInstance<T> =
        {
            isStateSaved = true
            RetainedInstance(lifecycleRegistry, instance)
        }

    stateKeeper.register(stateSupplier)

    lifecycle.subscribe(
        object : Lifecycle.Callbacks by lifecycleRegistry {
            override fun onCreate() {
                if (retainedInstance == null) {
                    lifecycleRegistry.onCreate()
                }
            }

            override fun onStart() {
                lifecycleRegistry.onStart()
                isStateSaved = false
            }

            override fun onDestroy() {
                stateKeeper.unregister(stateSupplier)
                if (!isStateSaved) {
                    lifecycleRegistry.onDestroy()
                }
            }
        }
    )

    return instance
}

inline fun <reified T : Any> StateKeeperProvider<Any>?.retainInstance(lifecycle: Lifecycle, noinline factory: (Lifecycle) -> T): T =
    retainInstance(lifecycle = lifecycle, key = T::class.toString(), factory = factory)

/**
 * Same as [retainInstance] but dedicated to retain [Store]s. Automatically disposes the [Store] at the end of [Lifecycle].
 */
fun <T : Store<*, *, *>> StateKeeperProvider<Any>?.retainStore(lifecycle: Lifecycle, key: String, factory: (Lifecycle) -> T): T =
    retainInstance(lifecycle = lifecycle, key = key) {
        val store = factory(it)
        it.doOnDestroy(store::dispose)
        store
    }

inline fun <reified T : Store<*, *, *>> StateKeeperProvider<Any>?.retainStore(lifecycle: Lifecycle, noinline factory: (Lifecycle) -> T): T =
    retainInstance(lifecycle = lifecycle) {
        val store = factory(it)
        it.doOnDestroy(store::dispose)
        store
    }

private class RetainedInstance<out T : Any>(
    val lifecycleRegistry: LifecycleRegistry,
    val instance: T
)
