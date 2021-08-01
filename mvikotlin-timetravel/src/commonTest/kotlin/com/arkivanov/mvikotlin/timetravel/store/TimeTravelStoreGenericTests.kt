package com.arkivanov.mvikotlin.timetravel.store

import com.arkivanov.mvikotlin.core.test.internal.StoreGenericTests
import com.arkivanov.mvikotlin.core.test.internal.TestBootstrapper
import com.arkivanov.mvikotlin.core.test.internal.TestExecutor
import com.arkivanov.mvikotlin.core.test.internal.reducer
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.isFrozen
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse

class TimeTravelStoreGenericTests : StoreGenericTests by StoreGenericTests(
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
) {

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun events_subscriber_not_frozen_WHEN_store_frozen_and_subscribed() {
        val store = TimeTravelStoreImpl(
            initialState = "initialState",
            bootstrapper = TestBootstrapper(),
            executorFactory = { TestExecutor() },
            reducer = reducer { it }
        )
            .apply {
                events(observer { process(it.type, it.value) })
                init()
            }
            .freeze()

        val list = ArrayList<TimeTravelStore.Event>()
        store.events(observer { list += it })

        assertFalse(list.isFrozen)
    }
}
