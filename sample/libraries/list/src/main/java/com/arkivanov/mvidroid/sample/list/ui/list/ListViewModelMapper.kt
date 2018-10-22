package com.arkivanov.mvidroid.sample.list.ui.list

import com.arkivanov.mvidroid.bind.MviViewModelMapper
import com.arkivanov.mvidroid.sample.list.component.ListStates
import io.reactivex.Observable

internal object ListViewModelMapper : MviViewModelMapper<ListStates, ListViewModel> {

    override fun map(states: ListStates): Observable<ListViewModel> =
        states
            .listStates
            .map { ListViewModel(items = it.items) }
}