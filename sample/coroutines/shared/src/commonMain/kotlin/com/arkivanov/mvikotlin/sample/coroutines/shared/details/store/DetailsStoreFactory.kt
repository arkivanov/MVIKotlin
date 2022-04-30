package com.arkivanov.mvikotlin.sample.coroutines.shared.details.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.State
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class DetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
    private val itemId: String,
) {

    fun create(): DetailsStore =
        object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoDetailsStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class Loaded(val data: TodoItem.Data) : Msg()
        object Finished : Msg()
        data class TextChanged(val text: String) : Msg()
        object DoneToggled : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Unit, State, Msg, Label>(mainContext) {
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

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.Loaded -> copy(data = msg.data)
                is Msg.Finished -> copy(isFinished = true)
                is Msg.TextChanged -> copy(data = data?.copy(text = msg.text))
                is Msg.DoneToggled -> copy(data = data?.copy(isDone = !data.isDone))
            }
    }
}
