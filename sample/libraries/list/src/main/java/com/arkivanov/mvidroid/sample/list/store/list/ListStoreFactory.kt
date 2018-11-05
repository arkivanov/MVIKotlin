package com.arkivanov.mvidroid.sample.list.store.list

import com.arkivanov.mvidroid.sample.list.dependency.ListDataSource
import com.arkivanov.mvidroid.sample.list.model.TodoItem
import com.arkivanov.mvidroid.sample.list.store.list.ListStore.Intent
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.MviStoreFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

internal class ListStoreFactory(
    private val factory: MviStoreFactory,
    private val dataSource: ListDataSource
) {

    fun create(): ListStore =
        object : MviStore<ListState, Intent, Nothing> by factory.create(
            name = "ListStore",
            initialState = ListState(),
            bootstrapper = Bootstrapper(),
            intentToAction = Action::ExecuteIntent,
            executorFactory = ::Executor,
            reducer = Reducer
        ), ListStore {
        }

    private sealed class Action {
        class ExecuteIntent(val intent: Intent) : Action()
        object Load : Action()
        class HandleUpdate(val update: ListDataSource.Update) : Action()
    }

    private sealed class Result {
        class Added(val item: TodoItem) : Result()
        class CompletedChanged(val id: Long, val isCompleted: Boolean) : Result()
        class Deleted(val id: Long) : Result()
        class Loaded(val items: List<TodoItem>) : Result()
        class Changed(val item: TodoItem) : Result()
    }

    private inner class Bootstrapper : MviBootstrapper<Action> {
        override fun bootstrap(dispatch: (Action) -> Unit): Disposable? =
            Observable
                .merge(
                    Observable.just(Action.Load),
                    dataSource
                        .updates
                        .map(Action::HandleUpdate)
                        .observeOn(AndroidSchedulers.mainThread())
                )
                .subscribe(dispatch)
    }

    private inner class Executor : MviExecutor<ListState, Action, Result, Nothing>() {
        override fun execute(action: Action): Disposable? =
            when (action) {
                is Action.ExecuteIntent ->
                    with(action) {
                        when (intent) {
                            is Intent.Add ->
                                dataSource
                                    .add(intent.text)
                                    .subscribe()

                            is Intent.SetCompleted ->
                                dataSource
                                    .setCompleted(intent.itemId, intent.isCompleted)
                                    .subscribe()

                            is Intent.Delete ->
                                dataSource
                                    .delete(intent.itemId)
                                    .subscribe()
                        }
                    }

                Action.Load ->
                    dataSource
                        .items
                        .map(Result::Loaded)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(::dispatch)

                is Action.HandleUpdate -> {
                    when (action.update) {
                        is ListDataSource.Update.Added -> dispatch(Result.Added(action.update.item))
                        is ListDataSource.Update.Changed -> dispatch(Result.Changed(action.update.item))
                        is ListDataSource.Update.Deleted -> dispatch(Result.Deleted(action.update.itemId))
                    }
                    null
                }
            }
    }

    private object Reducer : MviReducer<ListState, Result> {
        override fun ListState.reduce(result: Result): ListState =
            when (result) {
                is Result.Added -> copy(items = items.plus(result.item))
                is Result.CompletedChanged -> copy(items = items.set(result.id) { copy(isCompleted = result.isCompleted) })
                is Result.Deleted -> copy(items = items.filterNot { it.id == result.id })
                is Result.Loaded -> copy(items = result.items)
                is Result.Changed -> copy(items = items.set(result.item.id) { result.item })
            }

        private inline fun List<TodoItem>.set(id: Long, block: TodoItem.() -> TodoItem): List<TodoItem> =
            map { if (it.id == id) it.block() else it }
    }
}