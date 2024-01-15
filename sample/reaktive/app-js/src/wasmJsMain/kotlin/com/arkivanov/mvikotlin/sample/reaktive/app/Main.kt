package com.arkivanov.mvikotlin.sample.reaktive.app

import com.arkivanov.mvikotlin.core.store.create
import com.arkivanov.mvikotlin.timetravel.ExperimentalTimeTravelApi
import com.arkivanov.mvikotlin.timetravel.TimeTravelServer
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import kotlinx.browser.window

@OptIn(ExperimentalTimeTravelApi::class)
fun main() {
    TimeTravelServer().start()

    val store =
        TimeTravelStoreFactory().create<Intent, State>(
            name = "MyStore",
            initialState = State(),
            reducer = { intent ->
                when (intent) {
                    is Intent.Inc -> copy(count = count + intent.amount)
                }
            },
        )

    window.setTimeout(handler = { store.accept(Intent.Inc(amount = 4)).toJsReference() }, timeout = 10000)
}

sealed interface Intent {
    data class Inc(val amount: Int) : Intent
}

data class State(val count: Int = 0)
