package com.arkivanov.mvidroid.sample.app.store

import com.arkivanov.mvidroid.sample.app.BuildConfig
import com.arkivanov.mvidroid.store.factory.defaultstore.MviDefaultStoreFactory
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import com.arkivanov.mvidroid.store.factory.logging.MviLoggingStoreFactory
import com.arkivanov.mvidroid.store.factory.timetravel.MviTimeTravelStoreFactory

val storeFactory: MviStoreFactory by lazy {
    if (BuildConfig.DEBUG) {
        MviLoggingStoreFactory(delegate = MviTimeTravelStoreFactory)
    } else {
        MviDefaultStoreFactory
    }
}
