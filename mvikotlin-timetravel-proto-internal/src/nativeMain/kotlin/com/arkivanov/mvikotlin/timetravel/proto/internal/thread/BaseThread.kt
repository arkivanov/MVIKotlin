package com.arkivanov.mvikotlin.timetravel.proto.internal.thread

import kotlin.concurrent.AtomicInt
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.Worker

@OptIn(ObsoleteWorkersApi::class)
open class BaseThread {

    protected val worker = Worker.start(errorReporting = true)
    private val _isInterrupted = AtomicInt(0)
    val isInterrupted: Boolean get() = _isInterrupted.value != 0

    open fun interrupt() {
        if (_isInterrupted.compareAndSet(0, 1)) {
            worker.requestTermination(processScheduledJobs = false)
        }
    }
}
