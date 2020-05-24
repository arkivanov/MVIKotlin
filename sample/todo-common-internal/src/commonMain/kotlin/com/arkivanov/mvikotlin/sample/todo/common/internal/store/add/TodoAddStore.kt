package com.arkivanov.mvikotlin.sample.todo.common.internal.store.add

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State

interface TodoAddStore : Store<Intent, State, Label> {

    sealed class Intent : JvmSerializable {
        data class SetText(val text: String) : Intent()
        object Add : Intent()
    }

    data class State(
        val text: String = ""
    ) : JvmSerializable

    sealed class Label : JvmSerializable {
        data class Added(val item: TodoItem) : Label()
    }
}
