package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore
import kotlin.native.concurrent.ThreadLocal

/**
 * Returns a default implementation of the [TimeTravelController]
 */
@get:MainThread
val timeTravelController: TimeTravelController
    get() {
        assertOnMainThread()

        return TimeTravelControllerHolder.impl
    }

@MainThread
internal fun TimeTravelStore<*, *, *>.attachToController(name: String) {
    assertOnMainThread()

    TimeTravelControllerHolder.impl.attachStore(this, name)
}

@ThreadLocal
private object TimeTravelControllerHolder {
    val impl: TimeTravelControllerImpl = TimeTravelControllerImpl()
}
