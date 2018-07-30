package com.arkivanov.mvidroid.sample.ui.details

import com.arkivanov.mvidroid.bind.MviViewModelMapper
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsState
import io.reactivex.Observable

object DetailsViewModelMapper : MviViewModelMapper<Observable<TodoDetailsState>, DetailsViewModel> {

    override fun map(states: Observable<TodoDetailsState>): Observable<DetailsViewModel> =
        states.map {
            DetailsViewModel(
                text = it.text ?: "",
                isCompleted = it.isCompleted,
                isFinished = it.isFinished
            )
        }
}
