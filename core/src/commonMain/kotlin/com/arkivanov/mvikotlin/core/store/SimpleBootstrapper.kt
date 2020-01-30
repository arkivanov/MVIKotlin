package com.arkivanov.mvikotlin.core.store

class SimpleBootstrapper<Action>(
    private vararg val actions: Action
) : AbstractBootstrapper<Action>() {

    override fun invoke() {
        actions.forEach(::dispatch)
    }
}
