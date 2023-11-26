package com.arkivanov.mvikotlin.sample.coroutines.shared.main.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.ListStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.ListStore.State
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.database.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class ListStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
) {

    fun create(): ListStore =
        object : ListStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "ListStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.Init),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed interface Action : JvmSerializable {
        data object Init : Action
        data class SaveItem(val id: String) : Action
    }

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed interface Msg : JvmSerializable {
        data class Loaded(val items: List<TodoItem>) : Msg
        data class Deleted(val id: String) : Msg
        data class DoneToggled(val id: String) : Msg
        data class Added(val item: TodoItem) : Msg
        data class Changed(val id: String, val data: TodoItem.Data) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Nothing>(mainContext) {
        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.Init -> init()
                is Action.SaveItem -> saveItem(id = action.id, state = getState())
            }
        }

        private fun init() {
            scope.launch {
                val items = withContext(ioContext) { database.getAll() }
                dispatch(Msg.Loaded(items))
            }
        }

        private fun saveItem(id: String, state: State) {
            val item = state.items.find { it.id == id } ?: return

            scope.launch(ioContext) {
                database.save(id, item.data)
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.Delete -> delete(intent.id)
                is Intent.ToggleDone -> toggleDone(intent.id)
                is Intent.AddToState -> dispatch(Msg.Added(intent.item))
                is Intent.DeleteFromState -> dispatch(Msg.Deleted(intent.id))
                is Intent.UpdateInState -> dispatch(Msg.Changed(intent.id, intent.data))
            }
        }

        private fun delete(id: String) {
            dispatch(Msg.Deleted(id))

            scope.launch(ioContext) {
                database.delete(id)
            }
        }

        private fun toggleDone(id: String) {
            dispatch(Msg.DoneToggled(id))
            forward(Action.SaveItem(id = id))
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.Loaded -> copy(items = msg.items)
                is Msg.Deleted -> copy(items = items.filterNot { it.id == msg.id })
                is Msg.DoneToggled -> copy(items = items.update(msg.id) { copy(isDone = !isDone) })
                is Msg.Added -> copy(items = items + msg.item)
                is Msg.Changed -> copy(items = items.update(msg.id) { msg.data })
            }
    }
}
