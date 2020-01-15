package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

interface Reducer<State, in Result> {

    @MainThread
    fun State.reduce(result: Result): State
}
