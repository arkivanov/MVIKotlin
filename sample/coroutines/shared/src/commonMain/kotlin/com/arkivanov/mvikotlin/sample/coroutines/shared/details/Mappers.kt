package com.arkivanov.mvikotlin.sample.coroutines.shared.details

import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Event
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Model
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.State

internal val stateToModel: (State) -> Model =
    { state ->
        Model(
            text = state.data?.text ?: "",
            isDone = state.data?.isDone ?: false,
        )
    }

internal val eventToIntent: (Event) -> Intent? =
    { event ->
        when (event) {
            is Event.TextChanged -> Intent.SetText(text = event.text)
            is Event.DoneClicked -> Intent.ToggleDone
            is Event.DeleteClicked -> Intent.Delete
        }
    }
