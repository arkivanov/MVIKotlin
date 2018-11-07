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
) : MviAbstractComponent<ListEvent, ListStates, Relay<Any>>(
    stores = listOf(
        MviStoreBundle(
            store = listStore,
            eventTransformer = ListStoreUiEventTransformer
        ),
        MviStoreBundle(
            store = redirectStore,
            eventTransformer = RedirectStoreUiEventTransformer
        )
    )
), ListComponent {

    override val states: ListStates =
        ListStates(
            listStates = listStore.states,
            redirectStates = redirectStore.states
        )

    private object ListStoreUiEventTransformer : (ListEvent) -> ListStore.Intent? {
        override fun invoke(event: ListEvent): ListStore.Intent? =
            when (event) {
                is ListEvent.OnAddItem -> ListStore.Intent.Add(event.text)
                is ListEvent.OnSetItemCompleted -> ListStore.Intent.SetCompleted(event.id, event.isCompleted)
                is ListEvent.OnDeleteItem -> ListStore.Intent.Delete(event.id)
                else -> null
            }
    }

    private object RedirectStoreUiEventTransformer : (ListEvent) -> RedirectStore.Intent<ListRedirect>? {
        override fun invoke(event: ListEvent): RedirectStore.Intent<ListRedirect>? =
            when (event) {
                is ListEvent.OnItemSelected -> RedirectStore.Intent(ListRedirect.ShowItemDetails(event.id))
                ListEvent.OnRedirectHandled -> RedirectStore.Intent(null)
                else -> null
            }
    }
}