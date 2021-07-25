package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getAndUpdate

internal class Serializer<in T>(
    private val onValue: (T) -> Unit
) {
    private val state = atomic(State<T>())

    fun onNext(value: T) {
        val oldState =
            state.getAndUpdate {
                if (it.isDraining) {
                    it.copy(queue = it.queue + value)
                } else {
                    it.copy(isDraining = true)
                }
            }

        if (!oldState.isDraining) {
            onValue(value)
            drain()
        }
    }

    private fun drain() {
        while (true) {
            val oldState =
                state.getAndUpdate {
                    if (it.queue.isNotEmpty()) {
                        it.copy(queue = it.queue.drop(1))
                    } else {
                        it.copy(isDraining = false)
                    }
                }

            if (oldState.queue.isEmpty()) {
                break
            }

            onValue(oldState.queue.first())
        }
    }

    private data class State<out T>(
        val queue: List<T> = emptyList(),
        val isDraining: Boolean = false
    )
}
