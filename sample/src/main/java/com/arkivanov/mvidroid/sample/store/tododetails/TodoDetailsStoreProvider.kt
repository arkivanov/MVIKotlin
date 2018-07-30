package com.arkivanov.mvidroid.sample.store.tododetails

import com.arkivanov.mvidroid.sample.datasource.todo.TodoDataSource
import com.arkivanov.mvidroid.sample.model.TodoItem
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsStore.Intent
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.component.MviSimpleBootstrapper
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Provider

class TodoDetailsStoreProvider @Inject constructor(
    private val factory: MviStoreFactory,
    private val params: TodoDetailsStoreParams,
    private val dataSource: TodoDataSource
) : Provider<TodoDetailsStore> {

    override fun get(): TodoDetailsStore =
        object : MviStore<TodoDetailsState, Intent, Nothing> by factory.create(
            initialState = TodoDetailsState(),
            bootstrapper = MviSimpleBootstrapper(Action.Load),
            intentToAction = Action::ExecuteIntent,
            executor = Executor(),
            reducer = Reducer
        ), TodoDetailsStore {

        }

    private sealed class Action {
        object Load : Action()
        class ExecuteIntent(val intent: Intent) : Action()
    }

    private sealed class Result {
        class Loaded(val item: TodoItem) : Result()
        class TextChanged(val text: String) : Result()
        class CompletedChanged(val isCompleted: Boolean) : Result()
        object Finish : Result()
    }

    private inner class Executor : MviExecutor<TodoDetailsState, Action, Result, Nothing>() {
        override fun invoke(action: Action): Disposable? =
            when (action) {
                is Action.Load ->
                    dataSource
                        .get(params.itemId)
                        .map(Result::Loaded)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(::dispatch)

                is Action.ExecuteIntent ->
                    with(action) {
                        when (intent) {
                            is Intent.HandleTextChanged -> {
                                dispatch(Result.TextChanged(intent.text))
                                null
                            }

                            is Intent.HandleCompletedChanged -> {
                                dispatch(Result.CompletedChanged(intent.isCompleted))
                                null
                            }

                            is Intent.HandleDeleted -> {
                                dispatch(Result.Finish)
                                null
                            }
                        }
                    }
            }
    }

    private object Reducer : MviReducer<TodoDetailsState, Result> {
        override fun TodoDetailsState.reduce(result: Result): TodoDetailsState =
            when (result) {
                is Result.Loaded ->
                    copy(
                        text = result.item.text,
                        isCompleted = result.item.isCompleted
                    )

                is Result.TextChanged -> copy(text = result.text)
                is Result.CompletedChanged -> copy(isCompleted = result.isCompleted)
                Result.Finish -> copy(isFinished = true)
            }
    }
}
