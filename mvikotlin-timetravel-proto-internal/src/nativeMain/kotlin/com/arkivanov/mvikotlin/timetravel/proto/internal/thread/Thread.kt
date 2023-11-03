package com.arkivanov.mvikotlin.timetravel.proto.internal.thread

import kotlin.native.concurrent.ObsoleteWorkersApi

@OptIn(ObsoleteWorkersApi::class)
abstract class Thread : BaseThread() {

    protected abstract fun run()

    fun start() {
        if (!isInterrupted) {
            worker.execute {
                run()
                interrupt()
            }
        }
    }
}
