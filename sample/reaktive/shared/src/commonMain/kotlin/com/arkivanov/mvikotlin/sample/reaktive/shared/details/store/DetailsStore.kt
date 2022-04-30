package com.arkivanov.mvikotlin.sample.reaktive.shared.details.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStore.Intent
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStore.Label
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStore.State

internal interface DetailsStore : Store<Intent, State, Label> {

    // Serializable only for exporting events in Time Travel, no need otherwise.
    sealed class Intent : JvmSerializable {
        data class SetText(val text: String) : Intent()
        object ToggleDone : Intent()
        object Delete : Intent()
    }

    data class State(
        val data: TodoItem.Data? = null,
        val isFinished: Boolean = false,
    ) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.

    // Serializable only for exporting events in Time Travel, no need otherwise.
    sealed class Label : JvmSerializable {
        data class Changed(val id: String, val data: TodoItem.Data) : Label()
        data class Deleted(val id: String) : Label()
    }
}
