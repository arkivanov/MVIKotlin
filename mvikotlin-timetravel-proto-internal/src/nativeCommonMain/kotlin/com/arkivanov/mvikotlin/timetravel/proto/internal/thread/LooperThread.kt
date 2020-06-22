package com.arkivanov.mvikotlin.timetravel.proto.internal.thread

abstract class LooperThread<in T> : AbstractThread() {

    protected abstract fun run(message: T)

    fun submit(message: T) {
        if (!isInterrupted) {
            worker.execute { run(message) }
        }
    }
}
