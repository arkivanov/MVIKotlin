package com.arkivanov.mvikotlin.core.statekeeper

import kotlin.reflect.KClass

/**
 * Represents a provider of typed [StateKeeper]s
 */
interface StateKeeperProvider<in T : Any> {

    /**
     * Provides instances of [StateKeeper] by key
     *
     * @param clazz a Kotlin class of values to be saved and restored by the [StateKeeper]
     * @param key a string key, must be unique within the provider instance. The default value is [KClass.toString].
     * @return an instance of the [StateKeeper]
     */
    fun <S : T> get(clazz: KClass<out S>, key: String = clazz.toString()): StateKeeper<S>
}
