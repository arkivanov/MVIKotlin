package com.arkivanov.mvidroid.sample.store.todolist

import com.arkivanov.mvidroid.sample.datasource.todo.TodoDataSource
import com.arkivanov.mvidroid.sample.model.TodoItem
import com.arkivanov.mvidroid.sample.store.todolist.TodoListStore.Intent
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.component.MviSimpleBootstrapper
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Provider

class TodoListStoreProvider @Inject constructor(
    private val factory: MviStoreFactory,
    private val dataSource: TodoDataSource
) : Provider<TodoListStore> {

    override fun get(): TodoListStore =
        object : MviStore<TodoListState, Intent, Nothing> by factory.create(
            initialState = TodoListState(),
            bootstrapper = MviSimpleBootstrapper(Action.Load),
            intentToAction = Action::ExecuteIntent,
            executor = Executor(),
            reducer = Reducer
        ), TodoListStore {
        }

    private sealed class Action {
        object Load : Action()
        class ExecuteIntent(val intent: Intent) : Action()
    }

    private sealed class Result {
        class Loaded(val items: List<TodoItem>) : Result()
        class Added(val item: TodoItem) : Result()
        class TextChanged(val id: Long, val text: String) : Result()
        class CompletedChanged(val id: Long, val isCompleted: Boolean) : Result()
        class Remove(val id: Long) : Result()
    }

    private inner class Executor : MviExecutor<TodoListState, Action, Result, Nothing>() {
        override fun invoke(action: Action): Disposable? =
            when (action) {
                Action.Load ->
                    dataSource
                        .load()
                        .map(Result::Loaded)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(::dispatch)

                is Action.ExecuteIntent ->
                    with(action) {
                        when (intent) {
                            is Intent.AddItem ->
                                dataSource
                                    .add(intent.text)
                                    .map(Result::Added)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(::dispatch)

                            is Intent.HandleItemTextChanged -> {
                                dispatch(Result.TextChanged(intent.id, intent.text))
                                null
                            }

                            is Intent.HandleItemCompletedChanged -> {
                                dispatch(Result.CompletedChanged(intent.id, intent.isCompleted))
                                null
                            }

                            is Intent.HandleItemDeleted -> {
                                dispatch(Result.Remove(intent.id))
                                null
                            }
                        }
                    }
            }
    }

    private object Reducer : MviReducer<TodoListState, Result> {
        override fun TodoListState.reduce(result: Result): TodoListState =
            when (result) {
                is Result.Loaded -> copy(items = result.items)
                is Result.Added -> copy(items = items.plus(result.item))
                is Result.TextChanged -> updateItem(result.id) { copy(text = result.text) }
                is Result.CompletedChanged -> updateItem(result.id) { copy(isCompleted = result.isCompleted) }
                is Result.Remove -> copy(items = items.filterNot { it.id == result.id })
            }

        private inline fun TodoListState.updateItem(id: Long, func: TodoItem.() -> TodoItem): TodoListState =
            copy(
                items = items.map {
                    it.takeIf { it.id == id }?.func() ?: it
                }
            )
    }
}
