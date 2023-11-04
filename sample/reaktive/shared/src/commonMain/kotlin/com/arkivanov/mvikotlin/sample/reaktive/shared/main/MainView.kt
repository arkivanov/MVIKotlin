package com.arkivanov.mvikotlin.sample.reaktive.shared.main

import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Model

interface MainView : MviView<Model, Event> {

    data class Model(
        val items: List<Item>,
        val text: String,
    ) {
        // No-arg constructor for Swift.
        constructor() : this(
            items = emptyList(),
            text = "",
        )

        data class Item(
            val id: String,
            val text: String,
            val isDone: Boolean,
        )
    }

    sealed class Event {
        data class ItemClicked(val id: String) : Event()
        data class ItemDoneClicked(val id: String) : Event()
        data class ItemDeleteClicked(val id: String) : Event()
        data class TextChanged(val text: String) : Event()
        data object AddClicked : Event()
    }
}
