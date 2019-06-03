package com.arkivanov.mvidroid.sample.details

import android.arch.lifecycle.Lifecycle
import android.support.annotation.LayoutRes
import android.view.View
import com.arkivanov.mvidroid.bind.attachTo
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStore
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStoreFactory
import com.arkivanov.mvidroid.sample.details.dependency.DetailsDataSource
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.store.details.DetailsState
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStore
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStoreFactory
import com.arkivanov.mvidroid.sample.details.ui.details.DetailsView
import com.arkivanov.mvidroid.sample.details.ui.details.DetailsViewModel
import com.arkivanov.mvidroid.store.MviStoreFactory
import com.arkivanov.mvidroid.utils.attachTo
import com.arkivanov.mvidroid.utils.mapNotNull
import com.arkivanov.mvidroid.utils.subscribe
import com.arkivanov.mvidroid.utils.subscribeMvi

class DetailsComponent(
    itemId: Long,
    storeFactory: MviStoreFactory,
    detailsDataSource: DetailsDataSource,
    lifecycle: Lifecycle
) {

    private val detailsStore =
        DetailsStoreFactory(
            factory = storeFactory,
            itemId = itemId,
            dataSource = detailsDataSource
        )
            .create()
            .attachTo(lifecycle)

    private val redirectStore =
        RedirectStoreFactory(storeFactory)
            .create<DetailsRedirect>()
            .attachTo(lifecycle)

    init {
        detailsStore
            .labels
            .map { it.toRedirectStoreIntent() }
            .subscribe(redirectStore)
            .attachTo(lifecycle)
    }

    fun bindView(view: View, viewLifecycle: Lifecycle, redirectHandler: (DetailsRedirect) -> Unit) {
        val detailsView = DetailsView(view)

        detailsStore
            .states
            .map(::createDetailsViewModel)
            .subscribeMvi(detailsView)
            .attachTo(viewLifecycle)

        detailsView
            .events
            .map { it.toDetailsStoreIntent() }
            .subscribe(detailsStore)
            .attachTo(viewLifecycle)

        redirectStore
            .states
            .distinctUntilChanged()
            .mapNotNull(RedirectState<DetailsRedirect>::redirect)
            .subscribeMvi {
                it.use(redirectHandler)
            }
            .attachTo(viewLifecycle)
    }

    companion object {
        @LayoutRes
        val LAYOUT_ID = R.layout.layout_todo_details

        private fun DetailsStore.Label.toRedirectStoreIntent(): RedirectStore.Intent<DetailsRedirect> =
            when (this) {
                is DetailsStore.Label.Redirect -> RedirectStore.Intent(redirect = redirect)
            }

        private fun DetailsView.Event.toDetailsStoreIntent(): DetailsStore.Intent =
            when (this) {
                is DetailsView.Event.OnTextChanged -> DetailsStore.Intent.SetText(text)
                is DetailsView.Event.OnSetCompleted -> DetailsStore.Intent.SetCompleted(isCompleted)
                DetailsView.Event.OnDelete -> DetailsStore.Intent.Delete
            }

        private fun createDetailsViewModel(detailsState: DetailsState): DetailsViewModel =
            DetailsViewModel(
                text = detailsState.details?.text ?: "",
                isCompleted = detailsState.details?.isCompleted ?: false,
                isError = detailsState.isLoadingError
            )
    }
}
