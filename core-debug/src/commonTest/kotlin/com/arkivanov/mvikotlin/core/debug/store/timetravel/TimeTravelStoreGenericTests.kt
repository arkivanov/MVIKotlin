package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.internal.rx.observer
import com.arkivanov.mvikotlin.core.test.internal.StoreGenericTests
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class TimeTravelStoreGenericTests : StoreGenericTests by StoreGenericTests(
    storeFactory = { initialState, bootstrapper, executorFactory, reducer ->
        TimeTravelStoreImpl(
            name = "store",
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        ).apply {
            events(observer { eventProcessor.process(it.type, it.value) })
            init()
        }
    }
) {

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }
}
