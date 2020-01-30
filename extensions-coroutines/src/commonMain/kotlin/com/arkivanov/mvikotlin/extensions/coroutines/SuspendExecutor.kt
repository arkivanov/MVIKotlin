package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.BaseExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class SuspendExecutor<in Intent, in Action, Result, State, Label>(
    mainContext: CoroutineContext = Dispatchers.Main
) : BaseExecutor<Intent, Action, Result, State, Label>() {

    private val scope = CoroutineScope(mainContext)

    final override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)

        scope.launch {
            executeIntent(intent)
        }
    }

    protected open suspend fun executeIntent(intent: Intent) {
    }

    final override fun handleAction(action: Action) {
        super.handleAction(action)

        scope.launch {
            executeAction(action)
        }
    }

    protected open suspend fun executeAction(action: Action) {
    }

    override fun dispose() {
        scope.cancel()

        super.dispose()
    }
}
