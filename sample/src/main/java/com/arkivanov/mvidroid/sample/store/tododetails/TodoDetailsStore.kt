package com.arkivanov.mvidroid.sample.store.tododetails

import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsStore.Intent
import com.arkivanov.mvidroid.store.MviStore

interface TodoDetailsStore : MviStore<TodoDetailsState, Intent, Nothing> {

    sealed class Intent {
        class HandleTextChanged(val text: String) : Intent()
        class HandleCompletedChanged(val isCompleted: Boolean) : Intent()
        object HandleDeleted : Intent()
    }
}
