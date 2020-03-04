package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.view.ViewEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
val <Event : Any> ViewEvents<Event>.events: Flow<Event>
    get() = toFlow(ViewEvents<Event>::events)
