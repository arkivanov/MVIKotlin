package com.arkivanov.mvikotlin.keepers.statekeeper

import android.os.Bundle
import androidx.savedstate.SavedStateRegistry
import kotlin.reflect.KClass

@ExperimentalStateKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
internal abstract class AndroidStateKeeper<T : Any>(
    private val registry: SavedStateRegistry,
    private val clazz: KClass<out T>,
    private val key: String
) : StateKeeper<T> {

    override fun consume(): T? =
        registry
            .consumeRestoredStateForKey(key)
            ?.apply { classLoader = clazz.java.classLoader }
            ?.getValue(KEY)

    override fun register(supplier: () -> T) {
        registry.registerSavedStateProvider(key) {
            Bundle().apply {
                putValue(KEY, supplier())
            }
        }
    }

    override fun unregister() {
        registry.unregisterSavedStateProvider(key)
    }

    abstract fun <S : T> Bundle.getValue(key: String): S?

    abstract fun Bundle.putValue(key: String, value: T)

    private companion object {
        private const val KEY = "key"
    }
}
