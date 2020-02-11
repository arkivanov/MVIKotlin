package com.arkivanov.mvikotlin.sample.todo.reaktive

import com.arkivanov.mvikotlin.sample.todo.common.internal.BusEvent
import com.badoo.reaktive.subject.Relay
import com.badoo.reaktive.subject.publish.PublishSubject
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
internal val eventBus: Relay<BusEvent> = PublishSubject()
