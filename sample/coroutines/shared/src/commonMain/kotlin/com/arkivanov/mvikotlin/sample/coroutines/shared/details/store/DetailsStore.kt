package com.arkivanov.mvikotlin.sample.coroutines.shared.details.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

// Serializable only for exporting events in Time Travel, no need otherwise.
internal sealed class Intent : JvmSerializable {
    data class SetText(val text: String) : Intent()
    data object ToggleDone : Intent()
    data object Delete : Intent()
}

internal data class State(
    val data: TodoItem.Data? = null,
    val isFinished: Boolean = false,
) : JvmSerializable // Serializable only for exporting events in Time Travel, no need otherwise.

// Serializable only for exporting events in Time Travel, no need otherwise.
internal sealed class Label : JvmSerializable {
    data class Changed(val id: String, val data: TodoItem.Data) : Label()
    data class Deleted(val id: String) : Label()
}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed class Msg : JvmSerializable {
    data class Loaded(val data: TodoItem.Data) : Msg()
    data object Finished : Msg()
    data class TextChanged(val text: String) : Msg()
    data object DoneToggled : Msg()
}

private typealias ExecutorScope = CoroutineExecutorScope<State, Msg, Unit, Label>

/**
 * This builder function showcases the way of creating a [Store] using
 * the DSL API and *without* a dedicated interface.
 * This option may work better for smaller and simpler stores.
 * The [Intent], [State] and [Label] classes are top-level, the returned
 * type is just the generic [Store] interface.
 */
internal fun StoreFactory.detailsStore(
    database: TodoDatabase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext,
    itemId: String,
): Store<Intent, State, Label> =
    create<Intent, Unit, Msg, State, Label>(
        name = "TodoDetailsStore",
        initialState = State(),
        bootstrapper = SimpleBootstrapper(Unit),
        executorFactory = coroutineExecutorFactory(mainContext) {
            fun ExecutorScope.save() {
                val data = state().data ?: return
                publish(Label.Changed(itemId, data))

                launch(ioContext) {
                    database.save(itemId, data)
                }
            }

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
        reducer = { msg: Msg ->
            when (msg) {
                is Msg.Loaded -> copy(data = msg.data)
                is Msg.Finished -> copy(isFinished = true)
                is Msg.TextChanged -> copy(data = data?.copy(text = msg.text))
                is Msg.DoneToggled -> copy(data = data?.copy(isDone = !data.isDone))
            }
        },
    )
