package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStoreAbstractFactory
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class TodoListStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase
) : TodoListStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Unit, Result, State, Nothing> = ExecutorImpl()

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Unit, Result, State, Nothing>() {
        override fun handleAction(action: Unit) {
            singleFromFunction(database::getAll)
                .subscribeOn(ioScheduler)
                .map(Result::Loaded)
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::dispatch)
        }

        override fun handleIntent(intent: Intent) {
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

        private fun delete(id: String) {
            dispatch(Result.Deleted(id))

            singleFromFunction { database.delete(id) }
                .subscribeOn(ioScheduler)
                .subscribeScoped()
        }

        private fun toggleDone(id: String) {
            dispatch(Result.DoneToggled(id))

            val item = state.items.find { it.id == id } ?: return

            completableFromFunction {
                database.save(id, item.data)
            }
                .subscribeOn(ioScheduler)
                .subscribeScoped()
        }
    }
}
