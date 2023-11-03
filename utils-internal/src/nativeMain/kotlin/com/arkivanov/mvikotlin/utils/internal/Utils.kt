package com.arkivanov.mvikotlin.utils.internal

import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@OptIn(ObsoleteWorkersApi::class)
fun <T> runOnBackgroundBlocking(block: () -> T): T {
    val endTime = TimeSource.Monotonic.markNow() + 5.seconds
    var result by atomic<T?>(null)
    var isFinished by atomic(false)

    val worker =
        runOnBackground {
            result = block()
            isFinished = true
        }

    while (!isFinished && endTime.hasNotPassedNow()) {
        // no-op
    }

    worker.requestTermination(processScheduledJobs = false)

    if (isFinished) {
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    fail("Timeout running on background")
}

@OptIn(ObsoleteWorkersApi::class)
fun runOnBackground(block: () -> Unit): Worker {
    val worker = Worker.start()
    worker.execute(TransferMode.SAFE, { block }) { it() }

    return worker
}
