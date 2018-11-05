package com.arkivanov.mvidroid.sample.common.ui.redirect

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import com.arkivanov.mvidroid.view.MviBaseView

class RedirectView<Redirect : Any>(
    private val handler: (Redirect) -> Unit
) : MviBaseView<RedirectState<Redirect>, OnRedirectHandledEvent>() {

    init {
        registerDiffByEquals(this, RedirectState<Redirect>::redirect) {
            if (it != null) {
                dispatch(OnRedirectHandledEvent)
                handler(it)
            }
        }
    }
}