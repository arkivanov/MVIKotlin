package com.arkivanov.mvikotlin.sample.reaktive.shared.details.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutorScope
import com.arkivanov.mvikotlin.extensions.reaktive.reaktiveExecutorFactory
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

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

private typealias ExecutorScope = ReaktiveExecutorScope<State, Msg, Unit, Label>

/**
 * This builder function showcases the way of creating a [Store] using
 * the DSL API and *without* a dedicated interface.
 * This option may work better for smaller and simpler stores.
 * The [Intent], [State] and [Label] classes are top-level, the returned
 * type is just the generic [Store] interface.
 */
internal fun StoreFactory.detailsStore(
    database: TodoDatabase,
    itemId: String,
): Store<Intent, State, Label> =
    create(
        name = "TodoDetailsStore",
        initialState = State(),
        bootstrapper = SimpleBootstrapper(Unit),
        executorFactory = reaktiveExecutorFactory {
            fun ExecutorScope.save() {
                val data = state().data ?: return
                publish(Label.Changed(itemId, data))

                completableFromFunction { database.save(itemId, data) }
                    .subscribeOn(ioScheduler)
                    .subscribeScoped()
            }

            onAction<Unit> {
                singleFromFunction { database.get(itemId) }
                    .subscribeOn(ioScheduler)
                    .map { it?.data?.let(Msg::Loaded) ?: Msg.Finished }
                    .observeOn(mainScheduler)
                    .subscribeScoped(onSuccess = ::dispatch)
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

                completableFromFunction { database.delete(itemId) }
                    .subscribeOn(ioScheduler)
                    .observeOn(mainScheduler)
                    .subscribeScoped { dispatch(Msg.Finished) }
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
