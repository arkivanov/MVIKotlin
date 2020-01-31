package com.arkivanov.mvikotlin.sample.todo.coroutines.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStoreAbstractFactory
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class TodoDetailsStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val itemId: String,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext
) : TodoDetailsStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Unit, Result, State, Label> = ExecutorImpl()

    private inner class ExecutorImpl : SuspendExecutor<Intent, Unit, Result, State, Label>(mainContext = mainContext) {
        override suspend fun executeAction(action: Unit) {
            withContext(ioContext) {
                database.get(itemId)
            }
                .let { it?.data?.let(Result::Loaded) ?: Result.Finished }
                .also(::dispatch)
        }

        override suspend fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.HandleTextChanged -> handleTextChanged(intent.text)
                is Intent.ToggleDone -> toggleDone()
                is Intent.Delete -> delete()
            }.let {}
        }

        private suspend fun handleTextChanged(text: String) {
            dispatch(Result.TextChanged(text))
            save()
        }

        private suspend fun toggleDone() {
            dispatch(Result.DoneToggled)
            save()
        }

        private suspend fun save() {
            val data = state.data ?: return
            publish(Label.Changed(itemId, data))

            withContext(ioContext) {
                database.save(itemId, data)
            }
        }

        private suspend fun delete() {
            publish(Label.Deleted(itemId))

            withContext(ioContext) {
                database.delete(itemId)
            }

            dispatch(Result.Finished)
        }
    }
}
