package com.arkivanov.mvikotlin.core.instancekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle

/**
 * Provides a way to retain instances (e.g. a `Store`'s instance).
 * A typical use case is Android Activity recreation due to configuration changes.
 */
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
