package com.arkivanov.mvikotlin.core.instancekeeper

/**
 * Represents a provider of typed [InstanceKeeper]s
 */
@ExperimentalInstanceKeeperApi
interface InstanceKeeperProvider {

    /**
     * Provides instances of [InstanceKeeper] by key
     *
     * @param key a key, must be unique within the provider instance
     * @return an instance of the [InstanceKeeper]
     */
    fun <T : Any> get(key: Any): InstanceKeeper<T>
}
