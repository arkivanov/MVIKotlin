package com.arkivanov.mvidroid.store.component

import com.arkivanov.kfunction.KConsumer
import io.reactivex.disposables.Disposable

/**
 * Simple implementation of [MviBootstrapper] which immediately dispatches the provided Actions one by one
 */
class MviSimpleBootstrapper<out Action : Any>(private vararg val actions: Action) : MviBootstrapper<Action> {

    override fun bootstrap(dispatch: KConsumer<Action>): Disposable? {
        actions.forEach(dispatch)
        return null
    }
}
