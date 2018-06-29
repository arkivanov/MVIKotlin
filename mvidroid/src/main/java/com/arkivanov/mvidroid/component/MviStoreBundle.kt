package com.arkivanov.mvidroid.component

import com.arkivanov.kfunction.KFunction
import com.arkivanov.mvidroid.store.MviStore

/**
 * A holder for Store and its UI Event and Label transformers, used in [MviAbstractComponent]
 *
 * @param store a Store
 * @param uiEventTransformer a function that transforms UI Events to Store's Intents, returns null if there is no corresponding mapping
 * @param labelTransformer a function that transforms Labels to Store's Intents, returns null if there is no corresponding mapping
 * @param isPersistent if true then this Store will not be disposed by this Boundary
 * @param Intent type of Store's Intents
 * @param UiEvent type of Boundary's UI Events
 */
class MviStoreBundle<Intent : Any, in UiEvent : Any>(
    val store: MviStore<*, Intent, *>,
    val uiEventTransformer: KFunction<UiEvent, Intent?>? = null,
    val labelTransformer: KFunction<Any, Intent?>? = null,
    val isPersistent: Boolean = false
)
