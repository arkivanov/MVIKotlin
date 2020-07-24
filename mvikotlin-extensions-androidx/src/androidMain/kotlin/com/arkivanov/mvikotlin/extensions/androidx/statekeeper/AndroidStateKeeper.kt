package com.arkivanov.mvikotlin.extensions.androidx.statekeeper

import android.os.Bundle
import androidx.savedstate.SavedStateRegistry
import com.arkivanov.mvikotlin.core.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeperProvider
import kotlin.reflect.KClass

@ExperimentalStateKeeperApi
internal abstract class AndroidStateKeeper<in T : Any>(
    private val registry: SavedStateRegistry
) : StateKeeperProvider<T> {

    override fun <S : T> get(clazz: KClass<out S>, key: String): StateKeeper<S> =
        object : StateKeeper<S> {
            override fun getState(): S? =
                registry
                    .consumeRestoredStateForKey(key)
                    ?.apply { classLoader = clazz.java.classLoader }
                    ?.getValue(KEY)

            override fun setSupplier(supplier: (() -> S)?) {
                if (supplier == null) {
                    registry.unregisterSavedStateProvider(key)
                } else {
                    registry.registerSavedStateProvider(key) { saveState(supplier) }
                }
            }

            private fun saveState(supplier: () -> S): Bundle =
                Bundle().apply {
                    putValue(KEY, supplier())
                }
        }

    abstract fun <S : T> Bundle.getValue(key: String): S?

    abstract fun Bundle.putValue(key: String, value: T)

    private companion object {
        private const val KEY = "key"
    }
}
