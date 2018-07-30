package com.arkivanov.mvidroid.sample.ui.list

import com.arkivanov.mvidroid.bind.MviViewModelMapper
import com.arkivanov.mvidroid.sample.component.list.ListStates
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

object ListViewModelMapper : MviViewModelMapper<ListStates, ListViewModel> {

    override fun map(states: ListStates): Observable<ListViewModel> =
        Observable.combineLatest(
            states.todoListStates,
            states.todoActionStates,
            BiFunction { todoListStates, todoActionStates ->
                ListViewModel(
                    items = todoListStates.items,
                    detailsRedirectItemId = todoActionStates.detailsRedirectItemId
                )
            }
        )
}
