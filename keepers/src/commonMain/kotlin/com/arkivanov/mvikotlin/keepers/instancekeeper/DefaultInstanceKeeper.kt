package com.arkivanov.mvikotlin.keepers.instancekeeper

/**
 * A simple [InstanceKeeper] implementation via `HashMap`
 */
@ExperimentalInstanceKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
class DefaultInstanceKeeper : InstanceKeeper {

    private val map = HashMap<Any, InstanceKeeper.Instance>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : InstanceKeeper.Instance> get(key: Any, factory: () -> T): T =
        map.getOrPut(key, factory) as T

    /**
     * Destroys and removes all currently retained `Instances`
     */
    fun destroy() {
        map.forEach { it.value.onDestroy() }
        map.clear()
    }
}
