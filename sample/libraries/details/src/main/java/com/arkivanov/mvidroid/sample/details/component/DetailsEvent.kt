package com.arkivanov.mvidroid.sample.details.component

sealed class DetailsEvent {

    class OnTextChanged(val text: String) : DetailsEvent()
    class OnSetCompleted(val isCompleted: Boolean) : DetailsEvent()
    object OnDelete : DetailsEvent()
    object OnRedirectHandled : DetailsEvent()
}