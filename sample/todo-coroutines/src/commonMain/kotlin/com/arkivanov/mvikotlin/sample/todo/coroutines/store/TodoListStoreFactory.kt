package com.arkivanov.mvikotlin.sample.todo.coroutines.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStoreAbstractFactory
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class TodoListStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext
) : TodoListStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Unit, Result, State, Nothing> = ExecutorImpl()

    private inner class ExecutorImpl : SuspendExecutor<Intent, Unit, Result, State, Nothing>(mainContext = mainContext) {
        override suspend fun executeAction(action: Unit) {
            withContext(ioContext) { database.getAll() }
                .let(Result::Loaded)
                .also(::dispatch)
        }

        override suspend fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.Delete -> delete(intent.id)
                is Intent.ToggleDone -> toggleDone(intent.id)
                is Intent.SelectItem -> dispatch(Result.SelectionChanged(intent.id))
                is Intent.UnselectItem -> dispatch(Result.SelectionChanged(null))
                is Intent.HandleAdded -> dispatch(Result.Added(intent.item))
                is Intent.HandleTextChanged -> dispatch(Result.TextChanged(intent.id, intent.text))
                is Intent.HandleDeleted -> dispatch(Result.Deleted(intent.id))
                is Intent.HandleItemChanged -> dispatch(Result.Changed(intent.id, intent.data))
            }.let {}
        }

        private suspend fun delete(id: String) {
            dispatch(Result.Deleted(id))

            withContext(ioContext) {
                database.delete(id)
            }
        }

        private suspend fun toggleDone(id: String) {
            dispatch(Result.DoneToggled(id))

            val item = state.items.find { it.id == id } ?: return

            withContext(ioContext) {
                database.save(id, item.data)
            }
        }
    }
}
