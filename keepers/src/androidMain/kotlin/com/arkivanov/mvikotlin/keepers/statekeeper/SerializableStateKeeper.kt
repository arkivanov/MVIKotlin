package com.arkivanov.mvikotlin.keepers.statekeeper

import android.os.Bundle
import androidx.savedstate.SavedStateRegistry
import java.io.Serializable
import kotlin.reflect.KClass

@ExperimentalStateKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
internal class SerializableStateKeeper<T : Serializable>(
    registry: SavedStateRegistry,
    clazz: KClass<out T>,
    key: String
) : AndroidStateKeeper<T>(registry, clazz, key) {

    @Suppress("UNCHECKED_CAST")
    override fun <S : T> Bundle.getValue(key: String): S? = getSerializable(key) as S?

    override fun Bundle.putValue(key: String, value: T) {
        putSerializable(key, value)
    }
}
