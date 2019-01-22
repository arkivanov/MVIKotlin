package com.arkivanov.mvidroid.store

import com.arkivanov.mvidroid.component.MviStoreBundle

/**
 * Wraps Store into [MviStoreBundle]
 */
fun <Intent : Any, ComponentEvent : Any> MviStore<*, Intent, *>.toBundle(
    eventMapper: ((ComponentEvent) -> Intent?)? = null,
    labelMapper: ((Any) -> Intent?)? = null,
    isPersistent: Boolean = false
): MviStoreBundle<Intent, ComponentEvent> =
    MviStoreBundle(
        store = this,
        eventMapper = eventMapper,
        labelMapper = labelMapper,
        isPersistent = isPersistent
    )
