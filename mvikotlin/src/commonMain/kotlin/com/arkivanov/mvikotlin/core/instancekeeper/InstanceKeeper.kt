package com.arkivanov.mvikotlin.core.instancekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle

/**
 * Provides a way to retain instances (e.g. a `Store`'s instance).
 * A typical use case is Android Activity recreation due to configuration changes.
 */
@Deprecated(
    "Use InstanceKeeper from the 'keepers' module",
    ReplaceWith("InstanceKeeper", "com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper")
)
@ExperimentalInstanceKeeperApi
interface InstanceKeeper<T : Any> {

    /**
     * A [Lifecycle] of the [InstanceKeeper]
     */
    val lifecycle: Lifecycle

    /**
     * Read/write property for instance preservation
     */
    var instance: T?
}
