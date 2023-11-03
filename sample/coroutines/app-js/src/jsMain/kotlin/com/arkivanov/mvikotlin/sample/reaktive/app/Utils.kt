package com.arkivanov.mvikotlin.sample.reaktive.app

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import react.useEffectOnce
import react.useMemo
import kotlin.coroutines.CoroutineContext

fun useLifecycle(): Lifecycle {
    val registry = useMemo { LifecycleRegistry() }

    useEffectOnce {
        registry.resume()
        cleanup(registry::destroy)
    }

    return registry
}

fun useInstanceKeeper(): InstanceKeeper = useMemo { InstanceKeeperDispatcher() }

fun useCoroutineScope(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) {
    useEffectOnce {
        val scope = CoroutineScope(context)
        scope.launch(block = block)
        cleanup(scope::cancel)
    }
}
