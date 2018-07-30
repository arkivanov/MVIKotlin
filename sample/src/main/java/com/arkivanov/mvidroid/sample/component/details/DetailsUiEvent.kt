package com.arkivanov.mvidroid.sample.component.details

sealed class DetailsUiEvent {

    class OnTextChanged(val text: String) : DetailsUiEvent()
    class OnCompletedChanged(val isCompleted: Boolean) : DetailsUiEvent()
    object OnDelete : DetailsUiEvent()
}
