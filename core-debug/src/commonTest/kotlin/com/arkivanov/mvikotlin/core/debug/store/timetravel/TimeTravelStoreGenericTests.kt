package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.internal.rx.observer
import com.arkivanov.mvikotlin.core.test.internal.StoreGenericTests
import com.arkivanov.mvikotlin.core.test.internal.StoreGenericTestsImpl
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class TimeTravelStoreGenericTests : StoreGenericTests by StoreGenericTestsImpl(
    storeFactory = { initialState, bootstrapper, executorFactory, reducer ->
        TimeTravelStore(
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
