package com.arkivanov.mvidroid.sample.app.store

import com.arkivanov.mvidroid.sample.app.BuildConfig
import com.arkivanov.mvidroid.store.MviStoreFactory
import com.arkivanov.mvidroid.store.defaultstore.MviDefaultStoreFactory
import com.arkivanov.mvidroid.store.logging.MviLoggingStoreFactory
import com.arkivanov.mvidroid.store.timetravel.MviTimeTravelStoreFactory

val storeFactory: MviStoreFactory by lazy {
    if (BuildConfig.DEBUG) {
        MviLoggingStoreFactory(delegate = MviTimeTravelStoreFactory)
    } else {
        MviDefaultStoreFactory
    }
}
