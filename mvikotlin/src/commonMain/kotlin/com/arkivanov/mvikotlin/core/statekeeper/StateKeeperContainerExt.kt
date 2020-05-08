package com.arkivanov.mvikotlin.core.statekeeper

fun <State : Any> StateKeeperContainer<State, *>.saveAndGet(outState: State): State {
    save(outState)

    return outState
}
