package com.arkivanov.mvidroid.sample.details.component

sealed class DetailsUiEvent {

    class OnTextChanged(val text: String) : DetailsUiEvent()
    class OnSetCompleted(val isCompleted: Boolean) : DetailsUiEvent()
    object OnDelete : DetailsUiEvent()
    object OnRedirectHandled : DetailsUiEvent()
}