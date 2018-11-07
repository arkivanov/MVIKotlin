package com.arkivanov.mvidroid.component

import com.arkivanov.mvidroid.store.MviStore

/**
 * A holder for Store and its View Event and Label transformers, used in [MviAbstractComponent]
 *
 * @param store a Store
 * @param eventTransformer a function that transforms View Events to Store's Intents, returns null if there is no corresponding mapping
 * @param labelTransformer a function that transforms Labels to Store's Intents, returns null if there is no corresponding mapping
 * @param isPersistent if true then this Store will not be disposed by Component
 * @param Intent type of Store's Intents
 * @param ComponentEvent type of Component's Events
 */
class MviStoreBundle<Intent : Any, in ComponentEvent : Any>(
    val store: MviStore<*, Intent, *>,
    val eventTransformer: ((ComponentEvent) -> Intent?)? = null,
    val labelTransformer: ((Any) -> Intent?)? = null,
    val isPersistent: Boolean = false
)
