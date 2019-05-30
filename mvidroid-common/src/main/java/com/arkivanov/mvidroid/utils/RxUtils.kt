package com.arkivanov.mvidroid.utils

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.CheckResult
import com.arkivanov.mvidroid.bind.MviLifecycleObserver
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.view.MviView
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import java.lang.ref.WeakReference

/**
 * Returns an Observable that applies a specified function to each item emitted by the source ObservableSource and
 * emits only non-null results of these function applications.
 *
 * @param mapper a function to apply to each item emitted by the ObservableSource
 * @param T input type
 * @param R output type
 * @return an Observable that emits only non-null items transformed by the specified function from the source ObservableSource
 */
inline fun <T : Any, R : Any> ObservableSource<out T>.mapNotNull(crossinline mapper: (T) -> R?): Observable<R> =
    Observable.wrap(this).flatMapMaybe { mapper(it)?.let { Maybe.just(it) } ?: Maybe.empty() }

/**
 * Stores a [WeakReference] to the [Disposable] and disposes it at the end of [Lifecycle]
 *
 * @param lifecycle the [Lifecycle] which [Lifecycle.Event.ON_DESTROY] event will be used to dispose the [Disposable]
 * @param T the concrete type of [Disposable]
 * @return this [Disposable]
 */
fun <T : Disposable> T.attachTo(lifecycle: Lifecycle): T {
    val ref = WeakReference(this)

    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                lifecycle.removeObserver(this)
                ref.get()?.dispose()
            }
        }
    )

    return this
}

/**
 * Subscribes the Consumer to this Observable on [MviLifecycleObserver.onStart] and
 * unsubscribes on [MviLifecycleObserver.onStop]
 *
 * @param consumer the Consumer to subscribe with
 * @param T the type of values
 * @return [MviLifecycleObserver]
 */
@CheckResult
fun <T : Any> Observable<out T>.subscribeMvi(consumer: Consumer<T>): MviLifecycleObserver =
    object : MviLifecycleObserver {
        private var disposable: Disposable? = null

        override fun onStart() {
            disposable = subscribe(consumer)
        }

        override fun onStop() {
            disposable?.dispose()
            disposable = null
        }

        override fun onDestroy() {
        }
    }

/**
 * See [subscribeMvi]
 */
@CheckResult
inline fun <T : Any> Observable<out T>.subscribeMvi(crossinline consumer: (T) -> Unit): MviLifecycleObserver =
    subscribeMvi(Consumer { consumer(it) })

/**
 * See [subscribeMvi]
 */
@CheckResult
fun <T : Any> Observable<out T>.subscribeMvi(view: MviView<T, *>): MviLifecycleObserver = subscribeMvi(view::bind)

/**
 * See [subscribeMvi]
 */
@CheckResult
fun <T : Any> Observable<out T>.subscribeMvi(store: MviStore<*, T, *>): MviLifecycleObserver = subscribeMvi(store::accept)

/**
 * Subscribes this [Observable] to the [MviView]. See [Observable.subscribe] for more details.
 */
@CheckResult
fun <T : Any> Observable<out T>.subscribe(view: MviView<T, *>): Disposable = subscribe(view::bind)

/**
 * Subscribes this [Observable] to the [MviStore]. See [Observable.subscribe] for more details.
 */
@CheckResult
fun <T : Any> Observable<out T>.subscribe(store: MviStore<*, T, *>): Disposable = subscribe(store::accept)
