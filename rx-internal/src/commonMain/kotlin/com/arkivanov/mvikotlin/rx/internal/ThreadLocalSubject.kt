package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.utils.internal.assertOnMainThread
import kotlin.native.concurrent.ThreadLocal

internal open class ThreadLocalSubject<T> : Subject<T> {

    init {
        @Suppress("ReplacePutWithAssignment", "LeakingThis") // This is safe in this particular case
        state[this] = MutableState<T>()
    }

    override val isActive: Boolean get() = state.containsKey(this)

    override fun subscribe(observer: Observer<T>): Disposable {
        val mutableState: MutableState<T>? = getMutableState()

        return if (mutableState == null) {
            observer.onComplete()
            Disposable().also(Disposable::dispose)
        } else {
            val disposable = disposable()
            mutableState.map += disposable to observer
            onSubscribed(observer)
            disposable
        }
    }

    private fun disposable(): Disposable =
        Disposable {
            assertOnMainThread()
            getMutableState()?.also { it.map -= this }
        }

    protected open fun onSubscribed(observer: Observer<T>) {
    }

    override fun onNext(value: T) {
        val mutableState = getMutableState() ?: return
        mutableState.queue.addLast(value)
        mutableState.drainIfNeeded()
    }

    override fun onComplete() {
        val mutableState = removeMutableState() ?: return
        mutableState.isCompleted = true
        mutableState.drainIfNeeded()
    }

    private fun MutableState<T>.drainIfNeeded() {
        if (!isDraining) {
            isDraining = true
            try {
                drain()
            } finally {
                isDraining = false
            }
        }
    }

    private fun MutableState<T>.drain() {
        while (queue.isNotEmpty()) {
            val value = queue.removeFirst()
            map.values.forEach { it.onNext(value) }
        }

        if (isCompleted) {
            map.forEach { (disposable, observer) ->
                disposable.dispose()
                observer.onComplete()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getMutableState(): MutableState<T>? = state[this] as MutableState<T>?

    @Suppress("UNCHECKED_CAST")
    private fun removeMutableState(): MutableState<T>? = state.remove(this) as MutableState<T>?

    @ThreadLocal
    private companion object {
        private val state: MutableMap<ThreadLocalSubject<*>, MutableState<*>> = HashMap()
    }

    private class MutableState<T> {
        var map: Map<Disposable, Observer<T>> = mapOf()
        val queue: ArrayDeque<T> = ArrayDeque()
        var isCompleted: Boolean = false
        var isDraining: Boolean = false
    }
}
