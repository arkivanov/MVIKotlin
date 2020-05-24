package com.arkivanov.mvikotlin.sample.todo.common.internal.store.details

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.State

interface TodoDetailsStore : Store<Intent, State, Label> {

    sealed class Intent : JvmSerializable {
        data class SetText(val text: String) : Intent()
        object ToggleDone : Intent()
        object Delete : Intent()
    }

    data class State(
        val data: TodoItem.Data? = null,
        val isFinished: Boolean = false
    ) : JvmSerializable

    sealed class Label : JvmSerializable {
        data class Changed(val id: String, val data: TodoItem.Data) : Label()
        data class Deleted(val id: String) : Label()
    }
}
