package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.system.getTimeMillis
import kotlin.test.fail

fun <T> runOnBackgroundBlocking(block: () -> T): T {
    val endMillis = getTimeMillis() + 5000L
    var result by atomic<T?>(null)
    var isFinished by atomic(false)

    val worker =
        runOnBackground {
            result = block()
            isFinished = true
        }

    while (!isFinished && (getTimeMillis() < endMillis)) {
        // no-op
    }

    worker.requestTermination(processScheduledJobs = false)

    if (isFinished) {
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    fail("Timeout running on background")
}

fun runOnBackground(block: () -> Unit): Worker {
    val worker = Worker.start()
    worker.execute(TransferMode.SAFE, { block.freeze() }) {
        it.invoke()
    }

    return worker
}
