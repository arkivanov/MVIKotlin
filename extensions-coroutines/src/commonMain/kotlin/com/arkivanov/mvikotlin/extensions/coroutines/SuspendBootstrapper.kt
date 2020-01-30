package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.AbstractBootstrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class SuspendBootstrapper<Action>(
    mainContext: CoroutineContext = Dispatchers.Main
) : AbstractBootstrapper<Action>() {

    private val scope = CoroutineScope(mainContext)

    final override fun invoke() {
        scope.launch {
            bootstrap()
        }
    }

    abstract suspend fun bootstrap()

    override fun dispose() {
        scope.cancel()

        super.dispose()
    }
}
