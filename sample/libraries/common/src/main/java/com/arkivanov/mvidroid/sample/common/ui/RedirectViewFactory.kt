package com.arkivanov.mvidroid.sample.common.ui

import com.arkivanov.mvidroid.bind.MviViewBundle
import com.arkivanov.mvidroid.bind.using
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import io.reactivex.Observable

fun <States : Any, UiEvent : Any, Redirect : Any> createRedirectViewBundle(
    handler: (Redirect) -> Unit,
    redirectHandledUiEvent: UiEvent,
    getRedirects: (States) -> Observable<RedirectState<Redirect>>
): MviViewBundle<States, *, UiEvent> =
    RedirectView(handler, redirectHandledUiEvent) using RedirectViewModelMapper(getRedirects)
