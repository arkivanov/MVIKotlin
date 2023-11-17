package com.arkivanov.mvikotlin.sample.reaktive.shared.details.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutorScope
import com.arkivanov.mvikotlin.extensions.reaktive.reaktiveExecutorFactory
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStore.Intent
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStore.Label
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStore.State
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

@OptIn(ExperimentalMviKotlinApi::class)
internal class DetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val itemId: String
) {

    fun create(): DetailsStore =
        object : DetailsStore, Store<Intent, State, Label> by storeFactory.create<Intent, Unit, Msg, State, Label>(
            name = "TodoDetailsStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = reaktiveExecutorFactory {
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
            reducer = { msg ->
                when (msg) {
                    is Msg.Loaded -> copy(data = msg.data)
                    is Msg.Finished -> copy(isFinished = true)
                    is Msg.TextChanged -> copy(data = data?.copy(text = msg.text))
                    is Msg.DoneToggled -> copy(data = data?.copy(isDone = !data.isDone))
                }
            }
        ) {}

    // Serializable only for exporting events in Time Travel, no need otherwise.
    private sealed class Msg : JvmSerializable {
        data class Loaded(val data: TodoItem.Data) : Msg()
        object Finished : Msg()
        data class TextChanged(val text: String) : Msg()
        object DoneToggled : Msg()
    }

    private fun ReaktiveExecutorScope<State, Msg, Label>.save() {
        val data = state.data ?: return
        publish(Label.Changed(itemId, data))

        completableFromFunction { database.save(itemId, data) }
            .subscribeOn(ioScheduler)
            .subscribeScoped()
    }
}
