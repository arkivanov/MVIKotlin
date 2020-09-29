package com.arkivanov.mvikotlin.keepers.instancekeeper

/**
 * Provides a way to retain instances (e.g. a `Store`'s instance).
 * A typical use case is Android Activity recreation due to configuration changes.
 */
@ExperimentalInstanceKeeperApi
interface InstanceKeeper {

    /**
     * Either returns a currently retained [Instance] or creates (and retains) a new one.
     *
     * @param factory a factory function, called when there is no retained `Instance` yet
     * @return either a currently retained `Instance` or a new one
     */
    fun <T : Instance> getOrCreate(key: Any, factory: () -> T): T

    /**
     * Represents a retained instance
     */
    interface Instance {
        /**
         * Called when the `Instance` is destroyed. Clean-up any resources associated with the `Instance`.
         */
        fun onDestroy()
    }
}
