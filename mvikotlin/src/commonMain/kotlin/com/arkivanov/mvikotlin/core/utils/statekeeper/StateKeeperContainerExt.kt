package com.arkivanov.mvikotlin.core.utils.statekeeper

fun <State : Any> StateKeeperContainer<State, *>.saveAndGet(outState: State): State {
    save(outState)

    return outState
}
