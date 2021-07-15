package com.arkivanov.mvikotlin.keepers.statekeeper

import android.os.Parcelable
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.reflect.KClass

@ExperimentalStateKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
fun SavedStateRegistryOwner.getParcelableStateKeeperRegistry(): StateKeeperRegistry<Parcelable> =
    object : StateKeeperRegistry<Parcelable> {
        override fun <S : Parcelable> get(clazz: KClass<out S>, key: String): StateKeeper<S> =
            ParcelableStateKeeper(registry = savedStateRegistry, clazz = clazz, key = key)
    }
