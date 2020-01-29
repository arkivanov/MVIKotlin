package com.arkivanov.mvikotlin.sample.todo.reaktive

import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.badoo.reaktive.subject.Relay
import com.badoo.reaktive.subject.publish.PublishSubject

internal val eventBus: Relay<BusEvent> = PublishSubject()

internal sealed class BusEvent {
    data class TodoItemAdded(val item: TodoItem) : BusEvent()
    data class TodoItemChanged(val id: String, val data: TodoItem.Data) : BusEvent()
    data class TodoItemDeleted(val id: String) : BusEvent()
}
