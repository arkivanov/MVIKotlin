package com.arkivanov.mvikotlin.core.timetravel.controller

import com.arkivanov.mvikotlin.core.timetravel.store.TimeTravelStore

private val timeTravelControllerImpl = TimeTravelControllerImpl()

val timeTravelController: TimeTravelController =
    timeTravelControllerImpl

internal fun attachStore(store: TimeTravelStore<*, *, *>) {
    timeTravelControllerImpl.attachStore(store)
}
