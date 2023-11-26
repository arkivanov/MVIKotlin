package com.arkivanov.mvikotlin.core.rx

import com.arkivanov.mvikotlin.core.utils.Lock
import com.arkivanov.mvikotlin.core.utils.synchronized

internal class Serializer<in T>(
    private val onValue: (T) -> Unit
) {

    private val lock = Lock()
    private val queue = ArrayDeque<T>()
    private var isDraining = false

    fun onNext(value: T) {
        lock.synchronized {
            queue.addLast(value)

            if (isDraining) {
                return
            }

            isDraining = true
        }

        drain()
    }

    private fun drain() {
        while (true) {
            val value =
                lock.synchronized {
                    if (queue.isEmpty()) {
                        isDraining = false
                        return
                    }

                    queue.removeFirst()
                }

            onValue(value)
        }
    }
}
