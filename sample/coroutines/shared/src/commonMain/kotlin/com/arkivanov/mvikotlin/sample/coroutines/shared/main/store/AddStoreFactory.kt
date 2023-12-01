package com.arkivanov.mvikotlin.sample.coroutines.shared.main.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.AddStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.AddStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.AddStore.State
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class AddStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
) {

    fun create(): AddStore =
        object : AddStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoAddStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class TextChanged(val text: String) : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>(mainContext) {
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

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.TextChanged -> copy(text = msg.text)
            }
    }
}
