package com.arkivanov.mvidroid.sample.details.ui.details

import com.arkivanov.mvidroid.sample.details.component.DetailsStates
import io.reactivex.Observable

internal object DetailsViewModelMapper : (DetailsStates) -> Observable<out DetailsViewModel> {

    override fun invoke(states: DetailsStates): Observable<out DetailsViewModel> =
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