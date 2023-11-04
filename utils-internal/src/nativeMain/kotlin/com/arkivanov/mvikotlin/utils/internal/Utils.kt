package com.arkivanov.mvikotlin.utils.internal

import kotlin.concurrent.Volatile
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@OptIn(ObsoleteWorkersApi::class)
fun <T> runOnBackgroundBlocking(block: () -> T): T {
    val endTime = TimeSource.Monotonic.markNow() + 5.seconds
    val holder = Holder<T>()

    val worker =
        runOnBackground {
            holder.result = block()
            holder.isFinished = true
        }

    while (!holder.isFinished && endTime.hasNotPassedNow()) {
        // no-op
    }

    worker.requestTermination(processScheduledJobs = false)

    if (holder.isFinished) {
        @Suppress("UNCHECKED_CAST")
        return holder.result as T
    }

    fail("Timeout running on background")
}

private class Holder<T> {
    @Volatile
    var result: T? = null

    @Volatile
    var isFinished: Boolean = false
}

@OptIn(ObsoleteWorkersApi::class)
fun runOnBackground(block: () -> Unit): Worker {
    val worker = Worker.start()
    worker.execute(TransferMode.SAFE, { block }) { it() }

    return worker
}
