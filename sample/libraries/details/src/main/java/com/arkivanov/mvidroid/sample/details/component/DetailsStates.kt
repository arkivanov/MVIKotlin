package com.arkivanov.mvidroid.sample.details.component

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.store.details.DetailsState
import io.reactivex.Observable

class DetailsStates(
    val detailsStates: Observable<out DetailsState>,
    val redirectStates: Observable<out RedirectState<DetailsRedirect>>
)