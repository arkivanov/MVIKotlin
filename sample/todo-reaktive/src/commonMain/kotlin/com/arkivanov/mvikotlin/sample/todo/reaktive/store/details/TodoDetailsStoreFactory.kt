package com.arkivanov.mvikotlin.sample.todo.reaktive.store.details

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.details.TodoDetailsStore.State
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class TodoDetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val itemId: String
) {

    fun create(): TodoDetailsStore =
        object : TodoDetailsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoEditStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::Executor,
            reducer = ReducerImpl
        ) {
        }

    private sealed class Result : JvmSerializable {
        data class Loaded(val data: TodoItem.Data) : Result()
        object Finished : Result()
        data class TextChanged(val text: String) : Result()
        object DoneToggled : Result()
    }

    private inner class Executor : ReaktiveExecutor<Intent, Unit, Result, State, Label>() {
        override fun handleAction(action: Unit) {
            singleFromFunction {
                database.get(itemId)
            }
                .subscribeOn(ioScheduler)
                .map { it?.data?.let(Result::Loaded) ?: Result.Finished }
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::dispatch)
        }

        override fun handleIntent(intent: Intent) {
            when (intent) {
                is Intent.HandleTextChanged -> handleTextChanged(intent.text)
                is Intent.ToggleDone -> toggleDone()
                is Intent.Delete -> delete()
            }.let {}
        }

        private fun handleTextChanged(text: String) {
            dispatch(Result.TextChanged(text))
            save()
        }

        fun toggleDone() {
            dispatch(Result.DoneToggled)
            save()
        }

        private fun save() {
            val data = state.data ?: return
            publish(Label.Changed(itemId, data))

            completableFromFunction {
                database.put(itemId, data)
            }
                .subscribeOn(ioScheduler)
                .subscribeScoped()
        }

        private fun delete() {
            publish(Label.Deleted(itemId))

            completableFromFunction {
                database.delete(itemId)
            }
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true) {
                    dispatch(Result.Finished)
                }
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Loaded -> copy(data = result.data)
                is Result.Finished -> copy(isFinished = true)
                is Result.TextChanged -> copy(data = data?.copy(text = result.text))
                is Result.DoneToggled -> copy(data = data?.copy(isDone = !data.isDone))
            }
    }
}
