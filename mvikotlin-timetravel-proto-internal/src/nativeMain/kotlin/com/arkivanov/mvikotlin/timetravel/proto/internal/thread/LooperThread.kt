package com.arkivanov.mvikotlin.timetravel.proto.internal.thread

import kotlin.native.concurrent.ObsoleteWorkersApi

@OptIn(ObsoleteWorkersApi::class)
abstract class LooperThread<in T> : BaseThread() {

    protected abstract fun run(message: T)

    fun submit(message: T) {
        if (!isInterrupted) {
            worker.execute { run(message) }
        }
    }
}
