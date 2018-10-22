package com.arkivanov.mvidroid.sample.details.component

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStoreFactory
import com.arkivanov.mvidroid.sample.details.dependency.DetailsDataSource
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStoreFactory
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import com.jakewharton.rxrelay2.PublishRelay

class DetailsComponentFactory(
    private val itemId: Long,
    private val storeFactory: MviStoreFactory,
    private val detailsDataSource: DetailsDataSource
) {

    fun create(): DetailsComponent =
        DetailsComponentImpl(
            labels = PublishRelay.create(),
            detailsStore = DetailsStoreFactory(storeFactory, itemId, detailsDataSource).create(),
            redirectStore = RedirectStoreFactory(storeFactory).create()
        )
}