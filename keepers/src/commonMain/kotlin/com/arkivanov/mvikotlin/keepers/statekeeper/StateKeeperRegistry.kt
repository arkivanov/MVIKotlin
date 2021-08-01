package com.arkivanov.mvikotlin.keepers.statekeeper

import kotlin.reflect.KClass

/**
 * Provides and manages [StateKeeper]s
 */
@ExperimentalStateKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
interface StateKeeperRegistry<in T : Any> {

    /**
     * Provides instances of [StateKeeper] by key
     *
     * @param clazz a Kotlin class of values to be saved and restored by the [StateKeeper], *must not* be a local or an anonymous class.
     * @param key a string key, must be unique within the registry instance. The default value is [KClass.toString].
     * @return a typed instance of the [StateKeeper]
     */
    fun <S : T> get(clazz: KClass<out S>, key: String = clazz.toString()): StateKeeper<S>
}
