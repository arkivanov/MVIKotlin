package com.arkivanov.mvikotlin.timetravel.client.internal

import com.badoo.reaktive.scheduler.overrideSchedulers

fun initMainScheduler(postToMainThread: (task: () -> Unit) -> Unit) {
    overrideSchedulers(main = { SimpleMainScheduler(postToMainThread) })
}
