package com.arkivanov.mvikotlin.sample.shared.store.list

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.sample.shared.database.TodoItem
import com.arkivanov.mvikotlin.sample.shared.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.shared.store.list.TodoListStore.State

internal interface TodoListStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        data class Delete(val id: String) : Intent()
        data class ToggleDone(val id: String) : Intent()
        data class SelectItem(val id: String) : Intent()
        object UnselectItem : Intent()
        data class HandleAdded(val item: TodoItem): Intent()
    }

    data class State(
        val items: List<TodoItem> = emptyList(),
        val selectedItemId: String? = null
    )
}
