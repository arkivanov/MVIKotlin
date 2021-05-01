package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.utils.internal.AtomicRef
import com.arkivanov.mvikotlin.utils.internal.IsolatedRef
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getAndUpdate
import com.arkivanov.mvikotlin.utils.internal.isMainThread

internal open class ThreadLocalSubject<T>(
    private val isOnMainThread: () -> Boolean = ::isMainThread
) : Subject<T> {

    private val stateRef: AtomicRef<State<T>?> = atomic(State())

    override val isActive: Boolean get() = stateRef.value != null

    override fun subscribe(observer: Observer<T>): Disposable {
        val disposable =
            Disposable {
                stateRef.getAndUpdate {
                    it?.copy(map = it.map - this)
                }
            }

        if (!isOnMainThread()) {
            observer.freeze()
        }

        val isolatedObserver = IsolatedRef(observer)

        val oldState =
            stateRef.getAndUpdate {
                it?.copy(map = it.map + (disposable to isolatedObserver))
            }

        if (oldState != null) {
            onSubscribed(observer)
        } else {
            observer.onComplete()
            disposable.dispose()
        }

        return disposable
    }

    protected open fun onSubscribed(observer: Observer<T>) {
    }

    override fun onNext(value: T) {
        val oldState =
            stateRef.getAndUpdate {
                it?.copy(
                    queue = if (it.isDraining) it.queue + value else it.queue,
                    isDraining = true
                )
            } ?: return

        if (!oldState.isDraining) {
            oldState.emitValue(value)
            drain()
        }
    }

    override fun onComplete() {
        val oldState =
            stateRef.getAndUpdate {
                it?.copy(
                    isCompleted = true,
                    isDraining = true
                )
            } ?: return

        if (!oldState.isDraining) {
            drain()
        }
    }

    private fun drain() {
        while (true) {
            val oldState =
                stateRef.getAndUpdate {
                    it?.takeUnless(State<*>::isCompleted)?.run {
                        copy(
                            queue = queue.drop(1),
                            isDraining = queue.isNotEmpty()
                        )
                    }
                } ?: return

            if (oldState.isCompleted) {
                oldState.queue.forEach { value ->
                    oldState.emitValue(value)
                }

                oldState.map.forEach { (disposable, observer) ->
                    disposable.dispose()
                    observer.valueOrNull?.onComplete()
                }

                return
            }

            val queue = oldState.queue.takeUnless(List<*>::isEmpty) ?: return
            oldState.emitValue(queue.first())
        }
    }

    private fun State<T>.emitValue(value: T) {
        map.values.forEach { it.valueOrNull?.onNext(value) }
    }

    private data class State<T>(
        val map: Map<Disposable, IsolatedRef<Observer<T>>> = emptyMap(),
        val queue: List<T> = emptyList(),
        val isCompleted: Boolean = false,
        val isDraining: Boolean = false
    )
}
