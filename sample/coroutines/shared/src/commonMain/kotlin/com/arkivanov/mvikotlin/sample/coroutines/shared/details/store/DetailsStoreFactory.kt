package com.arkivanov.mvikotlin.sample.coroutines.shared.details.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.State
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalMviKotlinApi::class)
internal class DetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
    private val itemId: String,
) {

    fun create(): DetailsStore =
        object : DetailsStore, Store<Intent, State, Label> by storeFactory.create<Intent, Unit, Msg, State, Label>(
            name = "TodoDetailsStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = coroutineExecutorFactory(mainContext) {
                onAction<Unit> {
                    launch {
                        val item: TodoItem? = withContext(ioContext) { database.get(itemId) }
                        dispatch(item?.data?.let(Msg::Loaded) ?: Msg.Finished)
                    }
                }

                onIntent<Intent.SetText> {
                    dispatch(Msg.TextChanged(it.text))
                    save()
                }

                onIntent<Intent.ToggleDone> {
                    dispatch(Msg.DoneToggled)
                    save()
                }

                onIntent<Intent.Delete> {
                    publish(Label.Deleted(itemId))

                    launch {
                        withContext(ioContext) { database.delete(itemId) }
                        dispatch(Msg.Finished)
                    }
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.Loaded -> copy(data = msg.data)
                    is Msg.Finished -> copy(isFinished = true)
                    is Msg.TextChanged -> copy(data = data?.copy(text = msg.text))
                    is Msg.DoneToggled -> copy(data = data?.copy(isDone = !data.isDone))
                }
            },
        ) {}


    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class Loaded(val data: TodoItem.Data) : Msg()
        object Finished : Msg()
        data class TextChanged(val text: String) : Msg()
        object DoneToggled : Msg()
    }

    private fun CoroutineExecutorScope<State, *, *, Label>.save() {
        val data = state.data ?: return
        publish(Label.Changed(itemId, data))

        launch(ioContext) {
            database.save(itemId, data)
        }
    }
}
