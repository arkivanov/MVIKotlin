package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.badoo.reaktive.scheduler.Scheduler
import javax.swing.SwingUtilities
import kotlin.time.Duration

/*
 * A very rough implementation for now
 */
internal class SwingMainScheduler : Scheduler {

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

        override fun submit(delay: Duration, period: Duration, task: () -> Unit) {
            require(delay == Duration.ZERO) { "Delay is not supported" }

            SwingUtilities.invokeLater(Runnable(task))
        }
    }
}
