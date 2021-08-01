package com.arkivanov.mvikotlin.timetravel.client.internal.utils

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.observer
import com.badoo.reaktive.base.invoke
import com.badoo.reaktive.subject.behavior.BehaviorObservable
import com.badoo.reaktive.subject.behavior.BehaviorSubject

internal fun <T : Any, S : Any> Store<*, T, *>.mapState(lifecycle: Lifecycle, mapper: (T) -> S): BehaviorObservable<S> {
    val subject = BehaviorSubject(mapper(state))

    lifecycle.subscribe(
        object : Lifecycle.Callbacks {
            private var disposable: Disposable? = null

            override fun onCreate() {
                disposable = states(observer { subject(mapper(it)) })
            }

            override fun onDestroy() {
                disposable?.dispose()
                disposable = null
            }
        }
    )

    return subject
}
