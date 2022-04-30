package com.arkivanov.mvikotlin.sample.reaktive.app

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.badoo.reaktive.disposable.scope.DisposableScope
import react.useEffectOnce
import react.useMemo

fun useLifecycle(): Lifecycle {
    val registry = useMemo { LifecycleRegistry() }

    useEffectOnce {
        registry.resume()
        cleanup(registry::destroy)
    }

    return registry
}

fun useInstanceKeeper(): InstanceKeeper = useMemo { InstanceKeeperDispatcher() }

fun useDisposableScope(block: DisposableScope.() -> Unit) {
    useEffectOnce {
        val scope = DisposableScope()
        scope.block()
        cleanup(scope::dispose)
    }
}
