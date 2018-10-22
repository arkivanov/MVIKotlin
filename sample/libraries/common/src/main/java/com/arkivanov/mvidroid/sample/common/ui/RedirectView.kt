package com.arkivanov.mvidroid.sample.common.ui

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import com.arkivanov.mvidroid.view.MviBaseView

internal class RedirectView<Redirect : Any, UiEvent : Any>(
    private val handler: (Redirect) -> Unit,
    private val redirectHandledUiEvent: UiEvent
) : MviBaseView<RedirectState<Redirect>, UiEvent>() {

    init {
        registerDiffByEquals(this, RedirectState<Redirect>::redirect) {
            if (it != null) {
                dispatch(redirectHandledUiEvent)
                handler(it)
            }
        }
    }
}