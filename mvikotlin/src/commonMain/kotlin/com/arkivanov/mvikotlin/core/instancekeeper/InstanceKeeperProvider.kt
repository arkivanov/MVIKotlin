package com.arkivanov.mvikotlin.core.instancekeeper

/**
 * Represents a provider of typed [InstanceKeeper]s
 */
@Deprecated(
    "Use InstanceKeeper from the 'keepers' module",
    ReplaceWith("InstanceKeeper", "com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper")
)
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
