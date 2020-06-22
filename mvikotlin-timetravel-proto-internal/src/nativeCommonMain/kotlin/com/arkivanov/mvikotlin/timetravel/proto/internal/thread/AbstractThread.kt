package com.arkivanov.mvikotlin.timetravel.proto.internal.thread

import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.Worker

abstract class AbstractThread {

    protected val worker = Worker.start(errorReporting = true)
    private val _isInterrupted = AtomicInt(0)
    val isInterrupted: Boolean get() = _isInterrupted.value != 0

    open fun interrupt() {
        if (!_isInterrupted.compareAndSet(0, 1)) {
            worker.requestTermination(processScheduledJobs = false)
        }
    }
}