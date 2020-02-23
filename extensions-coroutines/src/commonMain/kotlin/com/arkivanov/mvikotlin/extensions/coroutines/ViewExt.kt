package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.view.View
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
val <Event : Any> View<*, Event>.events: Flow<Event>
    get() = toFlow(View<*, Event>::events)
