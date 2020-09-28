package com.arkivanov.mvikotlin.core.instancekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.badoo.reaktive.utils.ensureNeverFrozen

/**
 * A simple instances container, implements [InstanceKeeperProvider]
 */
@Deprecated(
    "Use DefaultInstanceKeeper from the 'keepers' module",
    ReplaceWith("DefaultInstanceKeeper", "com.arkivanov.mvikotlin.keepers.instancekeeper.DefaultInstanceKeeper")
)
@ExperimentalInstanceKeeperApi
class InstanceContainer(
    private val lifecycle: Lifecycle
) : InstanceKeeperProvider {

    init {
        ensureNeverFrozen()
    }

    private val map = HashMap<Any, Any>()

    override fun <T : Any> get(key: Any): InstanceKeeper<T> =
        object : InstanceKeeper<T> {
            override val lifecycle: Lifecycle get() = this@InstanceContainer.lifecycle

            override var instance: T?
                @Suppress("UNCHECKED_CAST")
                get() = map[key] as? T
                set(value) {
                    if (value == null) {
                        map -= key
                    } else {
                        map[key] = value
                    }
                }
        }
}
