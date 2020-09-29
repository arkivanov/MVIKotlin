package com.arkivanov.mvikotlin.keepers.instancekeeper

import com.badoo.reaktive.utils.ensureNeverFrozen

/**
 * A simple [InstanceKeeper] implementation via `HashMap`
 */
@ExperimentalInstanceKeeperApi
class DefaultInstanceKeeper : InstanceKeeper {

    init {
        ensureNeverFrozen()
    }

    private val map = HashMap<Any, InstanceKeeper.Instance>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : InstanceKeeper.Instance> getOrCreate(key: Any, factory: () -> T): T =
        map.getOrPut(key, factory) as T

    /**
     * Destroys and removes all currently retained `Instances`
     */
    fun destroy() {
        map.forEach { it.value.onDestroy() }
        map.clear()
    }
}
