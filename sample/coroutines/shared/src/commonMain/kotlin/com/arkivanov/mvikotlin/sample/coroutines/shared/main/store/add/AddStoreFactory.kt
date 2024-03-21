package com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.State
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal fun StoreFactory.addStore(
    database: TodoDatabase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext,
): AddStore =
    object : AddStore, Store<Intent, State, Label> by create(
        name = "TodoAddStore",
        initialState = State(),
        executorFactory = {
            ExecutorImpl(
                database = database,
                mainContext = mainContext,
                ioContext = ioContext,
            )
        },
        reducer = { reduce(it) },
    ) {}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed class Msg : JvmSerializable {
    data class TextChanged(val text: String) : Msg()
}

private class ExecutorImpl(
    private val database: TodoDatabase,
    mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
) : CoroutineExecutor<Intent, Nothing, State, Msg, Label>(mainContext) {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.SetText -> dispatch(Msg.TextChanged(intent.text))
            is Intent.Add -> addItem()
        }.let {}
    }

    private fun addItem() {
        val text = state().text.takeUnless(String::isBlank) ?: return

        dispatch(Msg.TextChanged(""))

        scope.launch {
            val item = withContext(ioContext) { database.create(TodoItem.Data(text = text)) }
            publish(Label.Added(item))
        }
    }
}

private fun State.reduce(msg: Msg): State =
    when (msg) {
        is Msg.TextChanged -> copy(text = msg.text)
    }
