package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

interface Executor<in Intent, in Action, in State, out Result, out Label> {

    @MainThread
    fun init(callbacks: Callbacks<State, Result, Label>)

    @MainThread
    fun handleIntent(intent: Intent) {
    }

    @MainThread
    fun handleAction(action: Action) {
    }

    @MainThread
    fun dispose() {
    }

    interface Callbacks<out State, in Result, in Label> {
        val state: State

        @MainThread
        fun onResult(result: Result)

        @MainThread
        fun onLabel(label: Label)
    }
}
