package com.arkivanov.mvikotlin.sample.todo.common.internal

import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem

sealed class BusEvent {
    data class TodoItemAdded(val item: TodoItem) : BusEvent()
    data class TodoItemChanged(val id: String, val data: TodoItem.Data) : BusEvent()
    data class TodoItemDeleted(val id: String) : BusEvent()
}
