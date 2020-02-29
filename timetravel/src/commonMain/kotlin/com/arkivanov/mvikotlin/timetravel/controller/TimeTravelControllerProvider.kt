package com.arkivanov.mvikotlin.timetravel.controller

import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStore

val timeTravelController: TimeTravelController get() = TimeTravelControllerHolder.impl

internal fun attachTimeTravelStore(store: TimeTravelStore<*, *, *>) {
    TimeTravelControllerHolder.impl.attachStore(store)
}

private object TimeTravelControllerHolder {
    val impl = TimeTravelControllerImpl()
}
