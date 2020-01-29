package com.arkivanov.mvikotlin.sample.todo.reaktive.store.list

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.database.update
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.list.TodoListStore.State
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class TodoListStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase
) {

    fun create(): TodoListStore =
        object : TodoListStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "ListStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::Executor,
            reducer = ReducerImpl
        ) {
        }

    private sealed class Result : JvmSerializable {
        data class Loaded(val items: List<TodoItem>) : Result()
        data class Deleted(val id: String) : Result()
        data class DoneToggled(val id: String) : Result()
        data class SelectionChanged(val id: String?) : Result()
        data class Added(val item: TodoItem) : Result()
        data class TextChanged(val id: String, val text: String) : Result()
        data class Changed(val id: String, val data: TodoItem.Data) : Result()
    }

    private inner class Executor : ReaktiveExecutor<Intent, Unit, Result, State, Nothing>() {
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
                database.put(id, item.data)
            }
                .subscribeOn(ioScheduler)
                .subscribeScoped()
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Loaded -> copy(items = result.items)
                is Result.Deleted -> copy(items = items.filterNot { it.id == result.id })
                is Result.DoneToggled -> copy(items = items.update(result.id) { copy(isDone = !isDone) })
                is Result.SelectionChanged -> copy(selectedItemId = result.id)
                is Result.Added -> copy(items = items + result.item)
                is Result.TextChanged -> copy(items = items.update(result.id) { copy(text = result.text) })
                is Result.Changed -> copy(items = items.update(result.id) { result.data })
            }
    }
}
