package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import kotlin.native.concurrent.ThreadLocal

/**
 * Returns a default implementation of the [TimeTravelController]
 */
@get:MainThread
val timeTravelController: TimeTravelController
    get() = TimeTravelControllerHolder.impl

@ThreadLocal
internal object TimeTravelControllerHolder {
    val impl: TimeTravelControllerImpl by lazy {
        assertOnMainThread()
        TimeTravelControllerImpl()
    }
}
