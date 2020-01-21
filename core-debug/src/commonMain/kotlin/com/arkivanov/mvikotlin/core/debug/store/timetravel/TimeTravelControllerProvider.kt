package com.arkivanov.mvikotlin.core.debug.store.timetravel

private val timeTravelControllerImpl = TimeTravelControllerImpl()

val timeTravelController: TimeTravelController = timeTravelControllerImpl

internal fun attachStore(store: TimeTravelStore<*, *, *>) {
    timeTravelControllerImpl.attachStore(store)
}
