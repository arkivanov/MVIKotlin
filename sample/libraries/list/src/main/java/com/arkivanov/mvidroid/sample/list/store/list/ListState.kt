package com.arkivanov.mvidroid.sample.list.store.list

import com.arkivanov.mvidroid.sample.list.model.TodoItem

data class ListState(
    val items: List<TodoItem> = emptyList()
)