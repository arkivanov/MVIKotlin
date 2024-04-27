package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Executor

interface DefaultExecutorCallbacks<out State, in Message, in Action, in Label> : Executor.Callbacks<State, Message, Action, Label> {

    override fun onMessage(message: Message) {
        // no-op
    }

    override fun onAction(action: Action) {
        // no-op
    }

    override fun onLabel(label: Label) {
        // no-op
    }
}
