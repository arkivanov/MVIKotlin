package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.utils.internal.assertOnMainThread
import kotlin.native.concurrent.ThreadLocal

internal open class ThreadLocalSubject<T> : Subject<T> {

    init {
        @Suppress("ReplacePutWithAssignment", "LeakingThis") // This is safe in this particular case
        state[this] = LinkedHashMap()
    }

    override val isActive: Boolean get() = state.containsKey(this)

    override fun subscribe(observer: Observer<T>): Disposable {
        val map: MutableMap<Disposable, Observer<T>>? = state[this]?.cast()

        return if (map == null) {
            observer.onComplete()
            Disposable().also(Disposable::dispose)
        } else {
            val disposable = disposable()
            map[disposable] = observer
            onSubscribed(observer)
            disposable
        }
    }

    protected open fun onSubscribed(observer: Observer<T>) {
    }

    private fun disposable(): Disposable =
        Disposable {
            assertOnMainThread()
            state[this@ThreadLocalSubject]?.remove(this)
        }

    override fun onNext(value: T) {
        state[this]
            ?.cast()
            ?.values
            ?.forEach { it.onNext(value) }
    }

    override fun onComplete() {
        state
            .remove(this)
            ?.forEach { (disposable, observer) ->
                disposable.dispose()
                observer.onComplete()
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun MutableMap<Disposable, Observer<*>>.cast(): MutableMap<Disposable, Observer<T>> =
        this as MutableMap<Disposable, Observer<T>>

    @ThreadLocal
    private companion object {
        private val state: MutableMap<ThreadLocalSubject<*>, MutableMap<Disposable, Observer<*>>> = HashMap()
    }
}
