package com.arkivanov.mvikotlin.core.store

interface Reducer<State, in Result> {

    fun State.reduce(result: Result): State
}
