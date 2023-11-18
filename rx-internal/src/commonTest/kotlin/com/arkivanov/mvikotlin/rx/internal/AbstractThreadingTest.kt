package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getAndUpdate
import com.badoo.reaktive.completable.blockingAwait
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.merge
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.completable.timeout
import com.badoo.reaktive.scheduler.createIoScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import kotlin.test.AfterTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

abstract class AbstractThreadingTest {

    protected open val threadCount: Int = 8
    private val scheduler = createIoScheduler()

    @AfterTest
    open fun after() {
        scheduler.destroy()
    }

    protected fun race(block: (threadIndex: Int) -> Unit) {
        val latch = CountDownLatch(count = threadCount)

        List(threadCount) { threadIndex ->
            completableFromFunction {
                latch.countDown()
                latch.await(timeout = 5.seconds)
                block(threadIndex)
            }.subscribeOn(scheduler)
        }
            .merge()
            .timeout(timeout = 10.seconds, scheduler = mainScheduler)
            .blockingAwait()
    }

    private class CountDownLatch(count: Int) {
        private val count = atomic(count)

        fun countDown() {
            count.getAndUpdate { it - 1 }
        }

        fun await(timeout: Duration) {
            val endTime = TimeSource.Monotonic.markNow() + timeout

            while ((count.value > 0) && endTime.hasNotPassedNow()) {
                // no-op
            }

            if (count.value > 0) {
                error("Timeout waiting for latch")
            }
        }
    }
}
