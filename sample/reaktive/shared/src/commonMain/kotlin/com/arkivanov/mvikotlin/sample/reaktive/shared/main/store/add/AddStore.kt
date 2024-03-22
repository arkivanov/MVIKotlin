package com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore.Intent
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore.Label
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore.State

internal interface AddStore : Store<Intent, State, Label> {

    // Serializable only for exporting events in Time Travel, no need otherwise.
    sealed class Intent : JvmSerializable {
        data class SetText(val text: String) : Intent()
        data object Add : Intent()
    }

    data class State(
        val text: String = "",
    ) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.

    // Serializable only for exporting events in Time Travel, no need otherwise.
    sealed class Label : JvmSerializable {
        data class Added(val item: TodoItem) : Label()
    }
}
