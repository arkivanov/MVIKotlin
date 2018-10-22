package com.arkivanov.mvidroid.sample.list.component

import com.arkivanov.mvidroid.component.MviAbstractComponent
import com.arkivanov.mvidroid.component.MviStoreBundle
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStore
import com.arkivanov.mvidroid.sample.list.model.ListRedirect
import com.arkivanov.mvidroid.sample.list.store.list.ListStore
import com.jakewharton.rxrelay2.Relay

internal class ListComponentImpl(
    listStore: ListStore,
    redirectStore: RedirectStore<ListRedirect>
) : MviAbstractComponent<ListUiEvent, ListStates, Relay<Any>>(
    stores = listOf(
        MviStoreBundle(
            store = listStore,
            uiEventTransformer = ListStoreUiEventTransformer
        ),
        MviStoreBundle(
            store = redirectStore,
            uiEventTransformer = RedirectStoreUiEventTransformer
        )
    )
), ListComponent {

    override val states: ListStates =
        ListStates(
            listStates = listStore.states,
            redirectStates = redirectStore.states
        )

    private object ListStoreUiEventTransformer : (ListUiEvent) -> ListStore.Intent? {
        override fun invoke(event: ListUiEvent): ListStore.Intent? =
            when (event) {
                is ListUiEvent.OnAddItem -> ListStore.Intent.Add(event.text)
                is ListUiEvent.OnSetItemCompleted -> ListStore.Intent.SetCompleted(event.id, event.isCompleted)
                is ListUiEvent.OnDeleteItem -> ListStore.Intent.Delete(event.id)
                else -> null
            }
    }

    private object RedirectStoreUiEventTransformer : (ListUiEvent) -> RedirectStore.Intent<ListRedirect>? {
        override fun invoke(event: ListUiEvent): RedirectStore.Intent<ListRedirect>? =
            when (event) {
                is ListUiEvent.OnItemSelected -> RedirectStore.Intent(ListRedirect.ShowItemDetails(event.id))
                ListUiEvent.OnRedirectHandled -> RedirectStore.Intent(null)
                else -> null
            }
    }
}