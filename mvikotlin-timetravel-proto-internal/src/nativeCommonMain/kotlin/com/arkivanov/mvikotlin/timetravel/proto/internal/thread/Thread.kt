package com.arkivanov.mvikotlin.timetravel.proto.internal.thread

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
