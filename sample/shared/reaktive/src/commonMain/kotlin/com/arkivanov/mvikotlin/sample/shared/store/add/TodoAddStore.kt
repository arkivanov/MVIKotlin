package com.arkivanov.mvikotlin.sample.shared.store.add

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.sample.shared.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.shared.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.shared.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.shared.database.TodoItem

internal interface TodoAddStore : Store<Intent, State, Label> {

    sealed class Intent {
        data class HandleTextChanged(val text: String) : Intent()
        object Add : Intent()
    }

    data class State(
        val text: String = ""
    )

    sealed class Label {
        data class Added(val item: TodoItem) : Label()
    }
}
