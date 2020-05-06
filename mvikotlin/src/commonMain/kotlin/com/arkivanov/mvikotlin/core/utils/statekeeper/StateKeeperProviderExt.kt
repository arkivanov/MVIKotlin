package com.arkivanov.mvikotlin.core.utils.statekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import kotlin.reflect.KClass

/**
 * Same as [StateKeeperProvider.get] but uses [KClass.toString] as key
 */
inline fun <T : Any, reified S : T> StateKeeperProvider<T>.get(): StateKeeper<S> =
    get(S::class.toString())

/**
 * Provides a way to retain instances via [StateKeeper].
 * A typical use case is to retain instances on Android configuration change.
 *
 * The initial `instance` is created using the provided `factory` function that accepts a special [Lifecycle].
 * The special [Lifecycle] is same as the original one
 * but its [onDestroy][Lifecycle.onDestroy] method is not called when the `instance` is retained.
 * The `instance` is retained and restored together with its special [Lifecycle].
 * After the restoration the special [Lifecycle]'s [onCreate][Lifecycle.onCreate] method is also not called,
 * since it was not destroyed. The original [Lifecycle]'s [onDestroy][Lifecycle.onDestroy] method is called
 * when the original [Lifecycle] is destroyed and the `instance` is not retained.
 *
 * @param lifecycle an original [Lifecycle] to be used
 * @param key an optional key that should be used for `instance` preservation, see [StateKeeperProvider.get] for more information
 * @param factory a factory function that accepts the special [Lifecycle] and returns an `instance` to be retained later
 * @return either a retained `instance` or a new `instance` created by the `factory` function if there is no retained one
 */
fun <T : Any> StateKeeperProvider<Any>.retainInstance(lifecycle: Lifecycle, key: String? = null, factory: (Lifecycle) -> T): T {
    val stateKeeper: StateKeeper<RetainedInstance<T>> = if (key == null) get() else get(key)

    val retainedInstance: RetainedInstance<T>? = stateKeeper.state
    val lifecycleRegistry = retainedInstance?.lifecycleRegistry ?: LifecycleRegistry()
    val instance = retainedInstance?.instance ?: factory(lifecycleRegistry)

    var isStateSaved = false
    stateKeeper.register {
        isStateSaved = true
        RetainedInstance(lifecycleRegistry, instance)
    }

    lifecycle.subscribe(
        object : Lifecycle.Callbacks by lifecycleRegistry {
            override fun onCreate() {
                if (retainedInstance == null) {
                    lifecycleRegistry.onCreate()
                }
            }

            override fun onDestroy() {
                if (!isStateSaved) {
                    lifecycleRegistry.onDestroy()
                }
            }
        }
    )

    return instance
}

private class RetainedInstance<out T : Any>(
    val lifecycleRegistry: LifecycleRegistry,
    val instance: T
)
