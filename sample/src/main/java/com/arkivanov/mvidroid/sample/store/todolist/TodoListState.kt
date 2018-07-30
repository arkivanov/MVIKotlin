package com.arkivanov.mvidroid.sample.store.todolist

import com.arkivanov.mvidroid.sample.model.TodoItem

data class TodoListState(
    val items: List<TodoItem> = emptyList()
)
