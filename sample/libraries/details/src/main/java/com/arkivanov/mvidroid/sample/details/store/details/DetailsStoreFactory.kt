package com.arkivanov.mvidroid.sample.details.store.details

import com.arkivanov.mvidroid.sample.details.dependency.DetailsDataSource
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.model.TodoDetails
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStore.Intent
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStore.Label
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.component.MviSimpleBootstrapper
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

internal class DetailsStoreFactory(
    private val factory: MviStoreFactory,
    private val itemId: Long,
    private val dataSource: DetailsDataSource
) {

    fun create(): DetailsStore =
        object : MviStore<DetailsState, Intent, Label> by factory.create(
            name = "DetailsStore",
            bootstrapper = MviSimpleBootstrapper(Action.Load),
            intentToAction = Action::ExecuteIntent,
            initialState = DetailsState(),
            executorFactory = ::Executor,
            reducer = Reducer
        ), DetailsStore {
        }

    private sealed class Action {
        class ExecuteIntent(val intent: Intent) : Action()
        object Load : Action()
    }

    private sealed class Result {
        class Loaded(val details: TodoDetails) : Result()
        object LoadingError : Result()
        class TextChanged(val text: String) : Result()
        class CompletedChanged(val isCompleted: Boolean) : Result()
    }

    private inner class Executor : MviExecutor<DetailsState, Action, Result, Label>() {
        private var setTextDisposable: Disposable? = null

        override fun execute(action: Action): Disposable? =
            when (action) {
                is DetailsStoreFactory.Action.ExecuteIntent ->
                    with(action) {
                        when (intent) {
                            is DetailsStore.Intent.SetText -> {
                                dispatch(Result.TextChanged(intent.text))
                                setTextDisposable?.dispose()
                                dataSource
                                    .setText(itemId, intent.text)
                                    .startWith(Completable.timer(500L, TimeUnit.MILLISECONDS))
                                    .subscribe()
                                    .also { setTextDisposable = it }
                            }

                            is DetailsStore.Intent.SetCompleted -> {
                                dispatch(Result.CompletedChanged(intent.isCompleted))
                                dataSource
                                    .setCompleted(itemId, intent.isCompleted)
                                    .subscribe()
                            }

                            DetailsStore.Intent.Delete ->
                                dataSource
                                    .delete(itemId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { publish(Label.Redirect(DetailsRedirect.Finish)) }
                        }
                    }

                DetailsStoreFactory.Action.Load ->
                    dataSource
                        .load(itemId)
                        .map<Result>(Result::Loaded)
                        .defaultIfEmpty(Result.LoadingError)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(::dispatch)
            }
    }

    private object Reducer : MviReducer<DetailsState, Result> {
        override fun DetailsState.reduce(result: Result): DetailsState =
            when (result) {
                is Result.Loaded -> copy(details = result.details)
                Result.LoadingError -> copy(isLoadingError = true)
                is Result.TextChanged -> changeData { copy(text = result.text) }
                is Result.CompletedChanged -> changeData { copy(isCompleted = result.isCompleted) }
            }

        private inline fun DetailsState.changeData(transform: TodoDetails.() -> TodoDetails): DetailsState =
            details?.transform()?.let { copy(details = it) } ?: this
    }
}