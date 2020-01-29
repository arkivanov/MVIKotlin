package com.arkivanov.mvikotlin.sample.todo.common.database

inline fun TodoItem.update(func: TodoItem.Data.() -> TodoItem.Data): TodoItem = copy(data = data.func())

inline fun Iterable<TodoItem>.update(id: String, func: TodoItem.Data.() -> TodoItem.Data): List<TodoItem> =
    map { if (it.id == id) it.update(func) else it }
