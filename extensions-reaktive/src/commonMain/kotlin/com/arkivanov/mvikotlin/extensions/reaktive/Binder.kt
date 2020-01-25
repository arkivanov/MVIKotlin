package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.core.view.View
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.subscribe

fun bind(builder: BindingsBuilder.() -> Unit): Binder = BuilderBinder().also(builder)

interface BindingsBuilder {
    infix fun <T> Observable<T>.bindTo(consumer: (T) -> Unit)

    infix fun <T> Observable<T>.bindTo(consumer: ValueCallback<T>)

    infix fun <T> Observable<T>.bindTo(view: View<T, *>)

    infix fun <T : Any> Observable<T>.bindTo(store: Store<T, *, *>)
}

interface Binder {
    fun start()

    fun stop()
}

private class BuilderBinder : BindingsBuilder, Binder, CompositeDisposable() {
    private val bindings = ArrayList<Binding<*>>()

    override fun <T> Observable<T>.bindTo(consumer: (T) -> Unit) {
        bindings += Binding(this, consumer)
    }

    override fun <T> Observable<T>.bindTo(consumer: ValueCallback<T>) {
        this bindTo consumer::onNext
    }

    override fun <T> Observable<T>.bindTo(view: View<T, *>) {
        this bindTo {
            assertOnMainThread()
            view.render(it)
        }
    }

    override fun <T : Any> Observable<T>.bindTo(store: Store<T, *, *>) {
        this bindTo store::accept
    }

    override fun start() {
        bindings.forEach { start(it) }
    }

    private fun <T> start(binding: Binding<T>) {
        binding
            .source
            .subscribe(onNext = binding.consumer)
            .addTo(this)
    }

    override fun stop() {
        clear()
    }
}

private class Binding<T>(
    val source: Observable<T>,
    val consumer: (T) -> Unit
)
