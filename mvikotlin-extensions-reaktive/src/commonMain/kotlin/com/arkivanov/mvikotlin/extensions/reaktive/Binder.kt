package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.binder.attachTo
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.subscribe

/**
 * A builder function for the [Binder]
 *
 * @param builder the DSL block function
 *
 * @return a new instance of the [Binder]
 */
fun bind(builder: BindingsBuilder.() -> Unit): Binder =
    BuilderBinder()
        .also(builder)

/**
 * A builder function for the [Binder]. Also attaches the created [Binder] to the provided [Lifecycle].
 * See [Binder.attachTo(...)][com.arkivanov.mvikotlin.core.binder.attachTo] for more information.
 *
 * @param lifecycle a [Lifecycle] to attach the created [Binder] to
 * @param mode a [BinderLifecycleMode] to be used when attaching the created [Binder] to the [Lifecycle]
 * @param builder the DSL block function
 *
 * @return a new instance of the [Binder]
 */
fun bind(lifecycle: Lifecycle, mode: BinderLifecycleMode, builder: BindingsBuilder.() -> Unit): Binder =
    bind(builder)
        .attachTo(lifecycle, mode)

interface BindingsBuilder {

    /**
     * Creates a binding between this [Observable] and the provided `consumer`
     *
     * @receiver a stream of values
     * @param consumer a `consumer` of values
     */
    infix fun <T> Observable<T>.bindTo(consumer: (T) -> Unit)

    /**
     * Creates a binding between this [Observable] and the provided `consumer`
     *
     * @receiver a stream of values
     * @param consumer a `consumer` of values represented as [ValueCallback]
     */
    infix fun <T> Observable<T>.bindTo(consumer: ValueCallback<T>)

    /**
     * Creates a binding between this [Observable] and the provided [ViewRenderer]
     *
     * @receiver a stream of the `View Models`
     * @param viewRenderer a [ViewRenderer] that will consume the `View Models`
     */
    infix fun <Model : Any> Observable<Model>.bindTo(viewRenderer: ViewRenderer<Model>)

    /**
     * Creates a binding between this [Observable] and the provided [Store]
     *
     * @receiver a stream of the [Store] `States`
     * @param store a [Store] that will consume the `Intents`
     */
    infix fun <Intent : Any> Observable<Intent>.bindTo(store: Store<Intent, *, *>)
}

private class BuilderBinder : BindingsBuilder, Binder, CompositeDisposable() {
    private val bindings = ArrayList<Binding<*>>()

    override fun <T> Observable<T>.bindTo(consumer: (T) -> Unit) {
        bindings += Binding(this, consumer)
    }

    override fun <T> Observable<T>.bindTo(consumer: ValueCallback<T>) {
        this bindTo consumer::onNext
    }

    override fun <Model : Any> Observable<Model>.bindTo(viewRenderer: ViewRenderer<Model>) {
        this bindTo {
            assertOnMainThread()
            viewRenderer.render(it)
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
