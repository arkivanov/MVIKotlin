package com.arkivanov.mvidroid.sample.list

import android.arch.lifecycle.Lifecycle
import android.support.annotation.LayoutRes
import android.view.View
import com.arkivanov.mvidroid.bind.attachTo
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStore
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStoreFactory
import com.arkivanov.mvidroid.sample.list.dependency.ListDataSource
import com.arkivanov.mvidroid.sample.list.model.ListRedirect
import com.arkivanov.mvidroid.sample.list.store.list.ListState
import com.arkivanov.mvidroid.sample.list.store.list.ListStore
import com.arkivanov.mvidroid.sample.list.store.list.ListStoreFactory
import com.arkivanov.mvidroid.sample.list.ui.list.ListView
import com.arkivanov.mvidroid.sample.list.ui.list.ListViewModel
import com.arkivanov.mvidroid.store.MviStoreFactory
import com.arkivanov.mvidroid.utils.attachTo
import com.arkivanov.mvidroid.utils.mapNotNull
import com.arkivanov.mvidroid.utils.subscribe
import com.arkivanov.mvidroid.utils.subscribeMvi

class ListComponent(
    storeFactory: MviStoreFactory,
    listDataSource: ListDataSource,
    lifecycle: Lifecycle
) {

    private val listStore =
        ListStoreFactory(
            factory = storeFactory,
            dataSource = listDataSource
        )
            .create()
            .attachTo(lifecycle)

    private val redirectStore =
        RedirectStoreFactory(storeFactory)
            .create<ListRedirect>()
            .attachTo(lifecycle)

    fun bindView(view: View, viewLifecycle: Lifecycle, redirectHandler: (ListRedirect) -> Unit) {
        val listView = ListView(view)

        listStore
            .states
            .map(::createListViewModel)
            .subscribeMvi(listView)
            .attachTo(viewLifecycle)

        listView
            .events
            .mapNotNull { it.toListStoreIntent() }
            .subscribe(listStore)
            .attachTo(viewLifecycle)

        redirectStore
            .states
            .distinctUntilChanged()
            .mapNotNull(RedirectState<ListRedirect>::redirect)
            .subscribeMvi {
                it.use(redirectHandler)
            }
            .attachTo(viewLifecycle)

        listView
            .events
            .mapNotNull { it.toRedirectStoreIntent() }
            .subscribe(redirectStore)
            .attachTo(viewLifecycle)
    }

    companion object {
        @LayoutRes
        val LAYOUT_ID = R.layout.layout_todo_list

        private fun ListView.Event.toListStoreIntent(): ListStore.Intent? =
            when (this) {
                is ListView.Event.OnAddItem -> ListStore.Intent.Add(text)
                is ListView.Event.OnSetItemCompleted -> ListStore.Intent.SetCompleted(id, isCompleted)
                is ListView.Event.OnDeleteItem -> ListStore.Intent.Delete(id)
                else -> null
            }

        private fun ListView.Event.toRedirectStoreIntent(): RedirectStore.Intent<ListRedirect>? =
            when (this) {
                is ListView.Event.OnItemSelected -> RedirectStore.Intent(ListRedirect.ShowItemDetails(id))
                else -> null
            }

        private fun createListViewModel(listState: ListState): ListViewModel =
            ListViewModel(items = listState.items)
    }
}