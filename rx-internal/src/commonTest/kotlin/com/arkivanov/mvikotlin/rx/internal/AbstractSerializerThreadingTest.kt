package com.arkivanov.mvikotlin.rx.internal

import kotlin.test.Test
import kotlin.test.assertEquals

abstract class AbstractSerializerThreadingTest : AbstractThreadingTest() {

    protected abstract val iterationCount: Int

    @Test
    fun emits_all_values_synchronized() {
        var expectedSum = 0L

        repeat(threadCount) { threadIndex ->
            repeat(iterationCount) { iterationIndex ->
                expectedSum += (threadIndex * iterationCount + iterationIndex)
            }
        }

        repeat(10) {
            var sum = 0L
            var count = 0

            val serializer =
                Serializer<Int> {
                    sum += it
                    count++
                }

            race { threadIndex ->
                repeat(iterationCount) { iterationIndex ->
                    serializer.onNext(threadIndex * iterationCount + iterationIndex)
                }
            }

            assertEquals(expectedSum, sum)
            assertEquals(threadCount * iterationCount, count)
        }
    }
}
