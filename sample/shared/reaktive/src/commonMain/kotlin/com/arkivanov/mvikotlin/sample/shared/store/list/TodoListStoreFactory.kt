package com.arkivanov.mvikotlin.sample.shared.store.list

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.shared.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.shared.database.TodoItem
import com.arkivanov.mvikotlin.sample.shared.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.shared.store.list.TodoListStore.State
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.scheduler.singleScheduler
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
            bootstrapper = SimpleBootstrapper(Action.LoadAll),
            executorFactory = ::Executor,
            reducer = ReducerImpl
        ) {
        }

    private sealed class Action : JvmSerializable {
        object LoadAll : Action()
    }

    private sealed class Result : JvmSerializable {
        data class Loaded(val items: List<TodoItem>) : Result()
        data class Deleted(val id: String) : Result()
        data class Updated(val item: TodoItem) : Result()
        data class SelectionChanged(val id: String?) : Result()
        data class Added(val item: TodoItem) : Result()
    }

    private inner class Executor : ReaktiveExecutor<Intent, Action, State, Result, Nothing>() {
        override fun handleIntent(intent: Intent) {
            when (intent) {
                is Intent.Delete -> delete(intent.id)
                is Intent.ToggleDone -> toggleDone(intent.id)
                is Intent.SelectItem -> dispatch(Result.SelectionChanged(intent.id))
                is Intent.UnselectItem -> dispatch(Result.SelectionChanged(null))
                is Intent.HandleAdded -> dispatch(Result.Added(intent.item))
            }.let {}
        }

        override fun handleAction(action: Action) {
            when (action) {
                is Action.LoadAll -> loadAll()
            }.let {}
        }

        private fun loadAll() {
            singleFromFunction(database::getAll)
                .subscribeOn(singleScheduler)
                .map(Result::Loaded)
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::dispatch)
        }

        private fun delete(id: String) {
            singleFromFunction { database.delete(id) }
                .subscribeOn(singleScheduler)
                .map { Result.Deleted(id) }
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::dispatch)
        }

        private fun toggleDone(id: String) {
            val newItem =
                state
                    .items
                    .find { it.id == id }
                    ?.let { it.copy(isDone = !it.isDone) }
                    ?: return

            singleFromFunction { database.put(newItem) }
                .subscribeOn(singleScheduler)
                .map(Result::Updated)
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::dispatch)
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Loaded -> copy(items = result.items)
                is Result.Deleted -> copy(items = items.filterNot { it.id == result.id })
                is Result.Updated -> copy(items = items.map { if (it.id == result.item.id) result.item else it })
                is Result.SelectionChanged -> copy(selectedItemId = result.id)
                is Result.Added -> copy(items = items + result.item)
            }
    }
}
