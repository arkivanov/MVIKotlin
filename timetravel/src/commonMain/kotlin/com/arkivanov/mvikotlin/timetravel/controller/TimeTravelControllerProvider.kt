package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore

private val timeTravelControllerImpl = TimeTravelControllerImpl()

val timeTravelController: TimeTravelController =
    timeTravelControllerImpl

internal fun attachTimeTravelStore(store: TimeTravelStore<*, *, *>) {
    timeTravelControllerImpl.attachStore(store)
}
