package com.arkivanov.mvidroid.sample.list.component

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStoreFactory
import com.arkivanov.mvidroid.sample.list.dependency.ListDataSource
import com.arkivanov.mvidroid.sample.list.store.list.ListStoreFactory
import com.arkivanov.mvidroid.store.MviStoreFactory

class ListComponentFactory(
    private val storeFactory: MviStoreFactory,
    private val listDataSource: ListDataSource
) {

    fun create(): ListComponent =
        ListComponentImpl(
            listStore = ListStoreFactory(storeFactory, listDataSource).create(),
            redirectStore = RedirectStoreFactory(storeFactory).create()
        )
}