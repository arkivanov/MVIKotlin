package com.arkivanov.mvidroid.sample.list.store.list

import com.arkivanov.mvidroid.sample.list.model.TodoItem
import java.io.Serializable

data class ListState(
    val items: List<TodoItem> = emptyList()
) : Serializable