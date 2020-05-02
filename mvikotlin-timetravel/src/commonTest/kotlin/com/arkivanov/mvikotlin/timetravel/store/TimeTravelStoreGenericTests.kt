package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.test.internal.StoreGenericTests
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.rx.observer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class TimeTravelStoreGenericTests : StoreGenericTests by StoreGenericTests(
    storeFactory = { initialState, bootstrapper, executorFactory, reducer ->
        TimeTravelStoreImpl(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        ).apply {
            events(observer { process(it.type, it.value) })
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
