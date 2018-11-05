package com.arkivanov.mvidroid.sample.list.ui.list

import com.arkivanov.mvidroid.sample.list.component.ListStates
import io.reactivex.Observable

internal object ListViewModelMapper : (ListStates) -> Observable<out ListViewModel> {

    override fun invoke(states: ListStates): Observable<out ListViewModel> =
        states
            .listStates
            .map { ListViewModel(items = it.items) }
}