package com.arkivanov.mvikotlin.timetravel.client.internal

import com.badoo.reaktive.scheduler.Scheduler

/*
 * A very rough implementation for now
 */
internal class SimpleMainScheduler(
    private val postToMainThread: (task: () -> Unit) -> Unit
) : Scheduler {

    override fun destroy() {
        // no-op
    }

    override fun newExecutor(): Scheduler.Executor = ExecutorImpl()

    private inner class ExecutorImpl : Scheduler.Executor {
        override var isDisposed: Boolean = false

        override fun cancel() {
            // no-op
        }

        override fun dispose() {
            isDisposed = true
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            require(delayMillis == 0L) { "Delay is not supported" }

            postToMainThread(task)
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            throw NotImplementedError("Repeat is not supported")
        }
    }
}
