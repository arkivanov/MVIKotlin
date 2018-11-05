package com.arkivanov.mvidroid.sample.details.ui

import android.support.annotation.LayoutRes
import android.view.View
import com.arkivanov.mvidroid.bind.MviViewBundle
import com.arkivanov.mvidroid.sample.common.ui.redirect.RedirectView
import com.arkivanov.mvidroid.sample.details.R
import com.arkivanov.mvidroid.sample.details.component.DetailsEvent
import com.arkivanov.mvidroid.sample.details.component.DetailsStates
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.ui.details.DetailsView
import com.arkivanov.mvidroid.sample.details.ui.details.DetailsViewModelMapper

class DetailsViewFactory(
    private val root: View,
    private val redirectHandler: (DetailsRedirect) -> Unit
) {

    fun create(): List<MviViewBundle<DetailsStates, DetailsEvent>> =
        listOf(
            MviViewBundle.create(DetailsView(root), DetailsViewModelMapper),
            MviViewBundle.create(RedirectView(redirectHandler), DetailsStates::redirectStates) { DetailsEvent.OnRedirectHandled }
        )

    companion object {
        @LayoutRes
        val LAYOUT_ID = R.layout.layout_todo_details
    }
}