package com.arkivanov.mvidroid.sample.details.ui.details

import com.arkivanov.mvidroid.bind.MviViewModelMapper
import com.arkivanov.mvidroid.sample.details.component.DetailsStates
import io.reactivex.Observable

internal object DetailsViewModelMapper : MviViewModelMapper<DetailsStates, DetailsViewModel> {

    override fun map(states: DetailsStates): Observable<DetailsViewModel> =
        states
            .detailsStates
            .map {
                DetailsViewModel(
                    text = it.details?.text ?: "",
                    isCompleted = it.details?.isCompleted ?: false,
                    isError = it.isLoadingError
                )
            }
}