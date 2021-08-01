package com.arkivanov.mvikotlin.keepers.statekeeper

import android.os.Bundle
import android.os.Parcelable
import androidx.savedstate.SavedStateRegistry
import kotlin.reflect.KClass

@ExperimentalStateKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
internal class ParcelableStateKeeper<T : Parcelable>(
    registry: SavedStateRegistry,
    clazz: KClass<out T>,
    key: String
) : AndroidStateKeeper<T>(registry, clazz, key) {

    override fun <S : T> Bundle.getValue(key: String): S? = getParcelable(key)

    override fun Bundle.putValue(key: String, value: T) {
        putParcelable(key, value)
    }
}
