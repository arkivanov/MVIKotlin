package com.arkivanov.mvikotlin.main.store

import com.arkivanov.mvikotlin.core.test.internal.StoreThreadingTests
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class DefaultStoreThreadingTests : StoreThreadingTests(
    storeFactory = { initialState, bootstrapper, executorFactory, reducer ->
        DefaultStore(
            initialState = initialState,
            bootstrapper = bootstrapper,
            executor = executorFactory(),
            reducer = reducer
        )
    }
)
