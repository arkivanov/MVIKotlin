package com.arkivanov.mvidroid.sample.store.todoaction

import com.arkivanov.mvidroid.sample.datasource.todo.TodoDataSource
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore.Intent
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore.Label
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Provider

class TodoActionStoreProvider @Inject constructor(
    private val factory: MviStoreFactory,
    private val dataSource: TodoDataSource
) : Provider<TodoActionStore> {

    override fun get(): TodoActionStore =
        object : MviStore<TodoActionState, Intent, Label> by factory.createActionless(
            initialState = TodoActionState(),
            executor = Executor(),
            reducer = Reducer
        ), TodoActionStore {
        }

    private sealed class Result {
        class RedirectToDetails(val id: Long) : Result()
        object RedirectedToDetails : Result()
    }

    private inner class Executor : MviExecutor<TodoActionState, Intent, Result, Label>() {
        override fun invoke(action: Intent): Disposable? =
            when (action) {
                is Intent.ItemSelected -> {
                    dispatch(Result.RedirectToDetails(action.id))
                    null
                }

                Intent.HandleRedirectedToDetails -> {
                    dispatch(Result.RedirectedToDetails)
                    null
                }

                is Intent.SetText ->
                    dataSource
                        .setText(action.id, action.text)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { publish(Label.ItemTextChanged(action.id, action.text)) }

                is Intent.SetCompleted ->
                    dataSource
                        .setCompleted(action.id, action.isCompleted)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { publish(Label.ItemCompletedChanged(action.id, action.isCompleted)) }

                is Intent.Delete ->
                    dataSource
                        .delete(action.id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { publish(Label.ItemDeleted(action.id)) }
            }
    }

    private object Reducer : MviReducer<TodoActionState, Result> {
        override fun TodoActionState.reduce(result: Result): TodoActionState =
            when (result) {
                is Result.RedirectToDetails -> copy(detailsRedirectItemId = result.id)
                Result.RedirectedToDetails -> copy(detailsRedirectItemId = null)
            }
    }
}
