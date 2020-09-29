package com.arkivanov.mvikotlin.keepers.statekeeper

import android.os.Parcelable
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.reflect.KClass

@ExperimentalStateKeeperApi
fun SavedStateRegistryOwner.getParcelableStateKeeperRegistry(): StateKeeperRegistry<Parcelable> =
    object : StateKeeperRegistry<Parcelable> {
        override fun <S : Parcelable> get(clazz: KClass<out S>, key: String): StateKeeper<S> =
            ParcelableStateKeeper(registry = savedStateRegistry, clazz = clazz, key = key)
    }
