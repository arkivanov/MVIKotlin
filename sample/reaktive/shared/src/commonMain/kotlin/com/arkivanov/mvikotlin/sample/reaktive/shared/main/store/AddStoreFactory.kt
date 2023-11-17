package com.arkivanov.mvikotlin.sample.reaktive.shared.main.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.AddStore.Intent
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.AddStore.Label
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.AddStore.State
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class AddStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase
) {

    fun create(): AddStore =
        object : AddStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoAddStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class TextChanged(val text: String) : Msg()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SetText -> dispatch(Msg.TextChanged(intent.text))
                is Intent.Add -> addItem(getState())
            }.let {}
        }

        private fun addItem(state: State) {
            val text = state.text.takeUnless(String::isBlank) ?: return

            dispatch(Msg.TextChanged(""))

            singleFromFunction {
                database.create(TodoItem.Data(text = text))
            }
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .map(Label::Added)
                .subscribeScoped(onSuccess = ::publish)
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.TextChanged -> copy(text = msg.text)
            }
    }
}
