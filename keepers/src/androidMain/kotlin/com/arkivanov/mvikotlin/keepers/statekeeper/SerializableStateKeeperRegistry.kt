package com.arkivanov.mvikotlin.keepers.statekeeper

import androidx.savedstate.SavedStateRegistryOwner
import java.io.Serializable
import kotlin.reflect.KClass

@ExperimentalStateKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
fun SavedStateRegistryOwner.getSerializableStateKeeperRegistry(): StateKeeperRegistry<Serializable> =
    object : StateKeeperRegistry<Serializable> {
        override fun <S : Serializable> get(clazz: KClass<out S>, key: String): StateKeeper<S> =
            SerializableStateKeeper(registry = savedStateRegistry, clazz = clazz, key = key)
    }
