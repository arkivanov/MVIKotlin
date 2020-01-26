package com.arkivanov.mvikotlin.sample.shared

import com.arkivanov.mvikotlin.sample.shared.database.TodoItem
import com.badoo.reaktive.subject.Relay
import com.badoo.reaktive.subject.publish.PublishSubject

internal val eventBus: Relay<BusEvent> = PublishSubject()

internal sealed class BusEvent {
    data class TodoItemAdded(val item: TodoItem) : BusEvent()
}
