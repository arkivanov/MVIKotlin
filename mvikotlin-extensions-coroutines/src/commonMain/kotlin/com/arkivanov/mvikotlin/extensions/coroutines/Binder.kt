package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.binder.attachTo
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * A builder function for the [Binder]
 *
 * @param mainContext a main CoroutineContext, the default value is Dispatchers.Main
 * @param builder the DSL block function
 *
 * @return a new instance of the [Binder]
 */
fun bind(mainContext: CoroutineContext = Dispatchers.Main, builder: BindingsBuilder.() -> Unit): Binder =
    BuilderBinder(mainContext)
        .also(builder)

/**
 * A builder function for the [Binder]. Also attaches the created [Binder] to the provided [Lifecycle].
 * See [Binder.attachTo(...)][com.arkivanov.mvikotlin.core.binder.attachTo] for more information.
 *
 * @param lifecycle a [Lifecycle] to attach the created [Binder] to
 * @param mode a [BinderLifecycleMode] to be used when attaching the created [Binder] to the [Lifecycle]
 * @param mainContext a main CoroutineContext, the default value is Dispatchers.Main
 * @param builder the DSL block function
 *
 * @return a new instance of the [Binder]
 */
fun bind(
    lifecycle: Lifecycle,
    mode: BinderLifecycleMode,
    mainContext: CoroutineContext = Dispatchers.Main,
    builder: BindingsBuilder.() -> Unit
): Binder =
    bind(mainContext, builder)
        .attachTo(lifecycle, mode)

interface BindingsBuilder {

    /**
     * Creates a binding between this [Flow] and the provided `consumer`
     *
     * @receiver a stream of values
     * @param consumer a `consumer` of values
     */
    infix fun <T> Flow<T>.bindTo(consumer: suspend (T) -> Unit)

    /**
     * Creates a binding between this [Flow] and the provided [ViewRenderer]
     *
     * @receiver a stream of the `View Models`
     * @param viewRenderer a [ViewRenderer] that will consume the `View Models`
     */
    infix fun <Model : Any> Flow<Model>.bindTo(viewRenderer: ViewRenderer<Model>)

    /**
     * Creates a binding between this [Flow] and the provided [Store]
     *
     * @receiver a stream of the [Store] `States`
     * @param store a [Store] that will consume the `Intents`
     */
    infix fun <Intent : Any> Flow<Intent>.bindTo(store: Store<Intent, *, *>)
}

private class BuilderBinder(
    private val mainContext: CoroutineContext
) : BindingsBuilder, Binder {
    private val bindings = ArrayList<Binding<*>>()
    private var job: Job? = null

    override fun <T> Flow<T>.bindTo(consumer: suspend (T) -> Unit) {
        bindings += Binding(this, consumer)
    }

    override fun <Model : Any> Flow<Model>.bindTo(viewRenderer: ViewRenderer<Model>) {
        this bindTo {
            assertOnMainThread()
            viewRenderer.render(it)
        }
    }

    override fun <T : Any> Flow<T>.bindTo(store: Store<T, *, *>) {
        this bindTo { store.accept(it) }
    }

    override fun start() {
        job =
            GlobalScope.launch(mainContext) {
                bindings.forEach { binding ->
                    start(binding)
                }
            }
    }

    private fun <T> CoroutineScope.start(binding: Binding<T>) {
        launch {
            binding.source.collect {
                if (isActive) {
                    binding.consumer(it)
                }
            }
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
    }
}

private class Binding<T>(
    val source: Flow<T>,
    val consumer: suspend (T) -> Unit
)
