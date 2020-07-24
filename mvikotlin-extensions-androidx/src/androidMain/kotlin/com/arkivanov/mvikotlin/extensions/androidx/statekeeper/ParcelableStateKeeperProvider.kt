package com.arkivanov.mvikotlin.extensions.androidx.statekeeper

import android.os.Bundle
import android.os.Parcelable
import androidx.savedstate.SavedStateRegistryOwner
import com.arkivanov.mvikotlin.core.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeperProvider

@ExperimentalStateKeeperApi
fun SavedStateRegistryOwner.getParcelableStateKeeperProvider(): StateKeeperProvider<Parcelable> =
    object : AndroidStateKeeper<Parcelable>(savedStateRegistry) {
        override fun <S : Parcelable> Bundle.getValue(key: String): S? = getParcelable<S>(key)

        override fun Bundle.putValue(key: String, value: Parcelable) {
            putParcelable(key, value)
        }
    }
