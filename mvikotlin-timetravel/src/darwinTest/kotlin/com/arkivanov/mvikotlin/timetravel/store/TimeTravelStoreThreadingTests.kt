package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.test.internal.StoreThreadingTests
import com.arkivanov.mvikotlin.rx.observer
import kotlin.test.Test
import kotlin.test.fail

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
