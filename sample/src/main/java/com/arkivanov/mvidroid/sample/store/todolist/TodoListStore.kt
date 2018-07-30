package com.arkivanov.mvidroid.sample.store.todolist

import com.arkivanov.mvidroid.sample.store.todolist.TodoListStore.Intent
import com.arkivanov.mvidroid.store.MviStore

interface TodoListStore : MviStore<TodoListState, Intent, Nothing> {

    sealed class Intent {
        class AddItem(val text: String) : Intent()
        class HandleItemTextChanged(val id: Long, val text: String) : Intent()
        class HandleItemCompletedChanged(val id: Long, val isCompleted: Boolean) : Intent()
        class HandleItemDeleted(val id: Long) : Intent()
    }
}
