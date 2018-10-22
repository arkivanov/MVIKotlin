package com.arkivanov.mvidroid.sample.details.ui

import android.support.annotation.LayoutRes
import android.view.View
import com.arkivanov.mvidroid.bind.MviViewBundle
import com.arkivanov.mvidroid.bind.using
import com.arkivanov.mvidroid.sample.common.ui.createRedirectViewBundle
import com.arkivanov.mvidroid.sample.details.R
import com.arkivanov.mvidroid.sample.details.component.DetailsStates
import com.arkivanov.mvidroid.sample.details.component.DetailsUiEvent
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.ui.details.DetailsView
import com.arkivanov.mvidroid.sample.details.ui.details.DetailsViewModelMapper

class DetailsViewFactory(
    private val root: View,
    private val redirectHandler: (DetailsRedirect) -> Unit
) {

    fun create(): List<MviViewBundle<DetailsStates, *, DetailsUiEvent>> =
        listOf(
            DetailsView(root) using DetailsViewModelMapper,
            createRedirectViewBundle<DetailsStates, DetailsUiEvent, DetailsRedirect>(
                handler = redirectHandler,
                redirectHandledUiEvent = DetailsUiEvent.OnRedirectHandled,
                getRedirects = DetailsStates::redirectStates
            )
        )

    companion object {
        @LayoutRes
        val LAYOUT_ID = R.layout.layout_todo_details
    }
}