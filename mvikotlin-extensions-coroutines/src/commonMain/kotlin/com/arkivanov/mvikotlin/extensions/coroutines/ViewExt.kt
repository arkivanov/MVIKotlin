package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.view.ViewEvents
import kotlinx.coroutines.flow.Flow

/**
 * Returns a [Flow] that emits `View Events`
 */
val <Event : Any> ViewEvents<Event>.events: Flow<Event>
    get() = toFlow(ViewEvents<Event>::events)
