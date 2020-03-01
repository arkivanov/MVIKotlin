package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore
import kotlin.native.concurrent.ThreadLocal

@get:MainThread
val timeTravelController: TimeTravelController
    get() {
        assertOnMainThread()

        return TimeTravelControllerHolder.impl
    }

@MainThread
internal fun attachTimeTravelStore(store: TimeTravelStore<*, *, *>) {
    TimeTravelControllerHolder.impl.attachStore(store)
}

@ThreadLocal
private object TimeTravelControllerHolder {
    val impl = TimeTravelControllerImpl()
}
