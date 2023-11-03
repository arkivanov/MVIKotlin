package com.arkivanov.mvikotlin.rx.internal

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
