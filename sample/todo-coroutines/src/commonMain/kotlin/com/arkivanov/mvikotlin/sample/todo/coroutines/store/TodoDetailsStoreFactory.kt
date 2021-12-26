package com.arkivanov.mvikotlin.sample.todo.coroutines.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStoreAbstractFactory
import kotlinx.coroutines.launch
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

    override fun createExecutor(): Executor<Intent, Unit, State, Msg, Label> = ExecutorImpl()

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Msg, Label>(mainContext = mainContext) {
        override fun executeAction(action: Unit, getState: () -> State) {
            scope.launch {
                val item: TodoItem? = withContext(ioContext) { database.get(itemId) }
                dispatch(item?.data?.let(Msg::Loaded) ?: Msg.Finished)
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SetText -> handleTextChanged(intent.text, getState)
                is Intent.ToggleDone -> toggleDone(getState)
                is Intent.Delete -> delete()
            }.let {}
        }

        private fun handleTextChanged(text: String, state: () -> State) {
            dispatch(Msg.TextChanged(text))
            save(state())
        }

        private fun toggleDone(state: () -> State) {
            dispatch(Msg.DoneToggled)
            save(state())
        }

        private fun save(state: State) {
            val data = state.data ?: return
            publish(Label.Changed(itemId, data))

            scope.launch(ioContext) {
                database.save(itemId, data)
            }
        }

        private fun delete() {
            publish(Label.Deleted(itemId))

            scope.launch {
                withContext(ioContext) {
                    database.delete(itemId)
                }

                dispatch(Msg.Finished)
            }
        }
    }
}
