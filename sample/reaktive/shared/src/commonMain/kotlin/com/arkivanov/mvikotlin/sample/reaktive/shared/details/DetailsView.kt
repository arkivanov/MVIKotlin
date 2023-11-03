package com.arkivanov.mvikotlin.sample.reaktive.shared.details

import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.DetailsView.Event
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.DetailsView.Model

interface DetailsView : MviView<Model, Event> {

    data class Model(
        val text: String,
        val isDone: Boolean,
    ) {
        // No-arg constructor for Swift.
        constructor() : this(
            text = "",
            isDone = false,
        )
    }

    sealed class Event {
        data class TextChanged(val text: String) : Event()
        data object DoneClicked : Event()
        data object DeleteClicked : Event()
    }
}
