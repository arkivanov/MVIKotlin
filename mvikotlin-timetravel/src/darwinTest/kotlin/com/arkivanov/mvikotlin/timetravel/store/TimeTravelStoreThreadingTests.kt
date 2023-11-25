package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.test.internal.StoreThreadingTests

class TimeTravelStoreThreadingTests : StoreThreadingTests(
    storeFactory = { initialState, bootstrapper, executorFactory, reducer ->
        TimeTravelStoreImpl(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        ).apply {
            events(observer { process(it.type, it.value) })
        }
    }
)
