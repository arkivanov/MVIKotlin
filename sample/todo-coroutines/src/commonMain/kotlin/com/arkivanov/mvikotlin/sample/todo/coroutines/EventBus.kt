package com.arkivanov.mvikotlin.sample.todo.coroutines

import com.arkivanov.mvikotlin.sample.todo.common.internal.BusEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlin.native.concurrent.SharedImmutable

@ExperimentalCoroutinesApi
@SharedImmutable
internal val eventBus = BroadcastChannel<BusEvent>(Channel.BUFFERED)
