package com.arkivanov.mvidroid.component

import com.arkivanov.mvidroid.store.MviStore

/**
 * [MviComponent] is deprecated.
 *
 * A holder for Store and its View Event and Label mapper, used in [MviAbstractComponent]
 *
 * @param store a Store
 * @param eventMapper a function that converts View Events to Store's Intents, returns null if there is no corresponding mapping
 * @param labelMapper a function that converts Labels to Store's Intents, returns null if there is no corresponding mapping
 * @param isPersistent if true then this Store will not be disposed by Component
 * @param Intent type of Store's Intents
 * @param ComponentEvent type of Component's Events
 */
@Deprecated(message = "MviComponent is deprecated")
class MviStoreBundle<Intent : Any, in ComponentEvent : Any>(
    val store: MviStore<*, Intent, *>,
    val eventMapper: ((ComponentEvent) -> Intent?)? = null,
    val labelMapper: ((Any) -> Intent?)? = null,
    val isPersistent: Boolean = false
)
