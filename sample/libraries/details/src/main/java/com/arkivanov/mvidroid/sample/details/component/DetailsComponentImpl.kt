package com.arkivanov.mvidroid.sample.details.component

import com.arkivanov.mvidroid.component.MviAbstractComponent
import com.arkivanov.mvidroid.component.MviStoreBundle
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStore
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStore
import com.jakewharton.rxrelay2.Relay

internal class DetailsComponentImpl(
    labels: Relay<Any>,
    detailsStore: DetailsStore,
    redirectStore: RedirectStore<DetailsRedirect>
) : MviAbstractComponent<DetailsUiEvent, DetailsStates, Relay<Any>>(
    stores = listOf(
        MviStoreBundle(
            store = detailsStore,
            uiEventTransformer = DetailsStoreUiEventTransformer
        ),
        MviStoreBundle(
            store = redirectStore,
            uiEventTransformer = RedirectStoreUiEventTransformer,
            labelTransformer = RedirectStoreLabelTransformer
        )
    ),
    labels = labels
), DetailsComponent {

    override val states: DetailsStates =
        DetailsStates(
            detailsStates = detailsStore.states,
            redirectStates = redirectStore.states
        )

    private object DetailsStoreUiEventTransformer : (DetailsUiEvent) -> DetailsStore.Intent? {
        override fun invoke(event: DetailsUiEvent): DetailsStore.Intent? =
            when (event) {
                is DetailsUiEvent.OnTextChanged -> DetailsStore.Intent.SetText(event.text)
                is DetailsUiEvent.OnSetCompleted -> DetailsStore.Intent.SetCompleted(event.isCompleted)
                DetailsUiEvent.OnDelete -> DetailsStore.Intent.Delete
                else -> null
            }
    }

    private object RedirectStoreUiEventTransformer : (DetailsUiEvent) -> RedirectStore.Intent<DetailsRedirect>? {
        override fun invoke(event: DetailsUiEvent): RedirectStore.Intent<DetailsRedirect>? =
            when (event) {
                DetailsUiEvent.OnRedirectHandled -> RedirectStore.Intent(null)
                else -> null
            }
    }

    private object RedirectStoreLabelTransformer : (Any) -> RedirectStore.Intent<DetailsRedirect>? {
        override fun invoke(label: Any): RedirectStore.Intent<DetailsRedirect>? =
            when (label) {
                is DetailsStore.Label.Redirect -> RedirectStore.Intent(redirect = label.redirect)
                else -> null
            }
    }
}