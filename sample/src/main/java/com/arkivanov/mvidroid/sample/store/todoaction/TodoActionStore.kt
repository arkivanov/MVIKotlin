package com.arkivanov.mvidroid.sample.store.todoaction

import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore.Intent
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore.Label
import com.arkivanov.mvidroid.store.MviStore

interface TodoActionStore : MviStore<TodoActionState, Intent, Label> {

    sealed class Intent {
        class ItemSelected(val id: Long) : Intent()
        object HandleRedirectedToDetails : Intent()
        class SetText(val id: Long, val text: String) : Intent()
        class SetCompleted(val id: Long, val isCompleted: Boolean) : Intent()
        class Delete(val id: Long) : Intent()
    }

    sealed class Label {
        class ItemTextChanged(val id: Long, val text: String) : Label()
        class ItemCompletedChanged(val id: Long, val isCompleted: Boolean) : Label()
        class ItemDeleted(val id: Long) : Label()
    }
}
