package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.utils.internal.IsolatedRef
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.isMainThread
import com.arkivanov.mvikotlin.utils.internal.setValue

internal open class BaseSubject<T>(
    private val isOnMainThread: () -> Boolean = ::isMainThread
) : Subject<T> {

    private val serializer = Serializer(::onEvent)
    private var observers by atomic<Map<Disposable, IsolatedRef<Observer<T>>>?>(emptyMap())
    private val lock = Lock()

    override val isActive: Boolean get() = observers != null

    override fun subscribe(observer: Observer<T>): Disposable {
        if (!isOnMainThread()) {
            observer.freeze()
        }

        val disposable = Disposable { serializer.onNext(Event.OnDispose(this)) }

        lock.synchronized {
            serializer.onNext(Event.OnSubscribe(IsolatedRef(observer), disposable))
        }

        return disposable
    }

    override fun onNext(value: T) {
        lock.synchronized {
            serializer.onNext(Event.OnNext(value))
        }
    }

    override fun onComplete() {
        lock.synchronized {
            serializer.onNext(Event.OnComplete)
        }
    }

    private fun onEvent(event: Event<T>) {
        when (event) {
            is Event.OnSubscribe -> onSubscribeEvent(event.observer, event.disposable)
            is Event.OnNext -> onNextEvent(event.value)
            is Event.OnComplete -> onCompleteEvent()
            is Event.OnDispose -> onDisposeEvent(event.disposable)
        }.let {}
    }

    private fun onSubscribeEvent(observer: IsolatedRef<Observer<T>>, disposable: Disposable) {
        val currentObservers = observers
        if (currentObservers == null) {
            observer.value.onComplete()
            disposable.dispose()
        } else {
            observers = currentObservers + (disposable to observer)
            onAfterSubscribe(observer.value)
        }
    }

    protected open fun onAfterSubscribe(observer: Observer<T>) {
    }

    private fun onNextEvent(value: T) {
        onBeforeNext(value)

        observers?.values?.forEach {
            it.value.onNext(value)
        }
    }

    protected open fun onBeforeNext(value: T) {
    }

    private fun onCompleteEvent() {
        observers?.forEach { (disposable, observer) ->
            observer.value.onComplete()
            disposable.dispose()
        }

        observers = null
    }

    private fun onDisposeEvent(disposable: Disposable) {
        observers = observers?.minus(disposable)
    }

    private sealed class Event<out T> {
        class OnSubscribe<T>(
            val observer: IsolatedRef<Observer<T>>,
            val disposable: Disposable
        ) : Event<T>()

        class OnNext<out T>(val value: T) : Event<T>()
        object OnComplete : Event<Nothing>()
        class OnDispose(val disposable: Disposable) : Event<Nothing>()
    }
}
