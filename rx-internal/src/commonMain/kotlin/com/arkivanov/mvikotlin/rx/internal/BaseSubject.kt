package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer

internal open class BaseSubject<T> : Subject<T> {

    private val serializer = Serializer(::onEvent)
    private var observers: MutableMap<Disposable, Observer<T>>? = LinkedHashMap()
    private val lock = Lock()

    override val isActive: Boolean get() = observers != null

    override fun subscribe(observer: Observer<T>): Disposable {
        val disposable = Disposable { serializer.onNext(Event.OnDispose(this)) }

        lock.synchronized {
            serializer.onNext(Event.OnSubscribe(observer, disposable))
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

    private fun onSubscribeEvent(observer: Observer<T>, disposable: Disposable) {
        val currentObservers = observers
        if (currentObservers == null) {
            observer.onComplete()
            disposable.dispose()
        } else {
            currentObservers[disposable] = observer
            onAfterSubscribe(observer)
        }
    }

    protected open fun onAfterSubscribe(observer: Observer<T>) {
    }

    private fun onNextEvent(value: T) {
        onBeforeNext(value)

        observers?.values?.forEach {
            it.onNext(value)
        }
    }

    protected open fun onBeforeNext(value: T) {
    }

    private fun onCompleteEvent() {
        observers?.forEach { (disposable, observer) ->
            observer.onComplete()
            disposable.dispose()
        }

        observers = null
    }

    private fun onDisposeEvent(disposable: Disposable) {
        observers?.remove(disposable)
    }

    private sealed class Event<out T> {
        class OnSubscribe<T>(
            val observer: Observer<T>,
            val disposable: Disposable
        ) : Event<T>()

        class OnNext<out T>(val value: T) : Event<T>()
        data object OnComplete : Event<Nothing>()
        class OnDispose(val disposable: Disposable) : Event<Nothing>()
    }
}
