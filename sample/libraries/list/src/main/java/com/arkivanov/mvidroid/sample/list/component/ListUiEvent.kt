package com.arkivanov.mvidroid.sample.list.component

sealed class ListUiEvent {

    class OnAddItem(val text: String) : ListUiEvent()
    class OnSetItemCompleted(val id: Long, val isCompleted: Boolean) : ListUiEvent()
    class OnDeleteItem(val id: Long) : ListUiEvent()
    class OnItemSelected(val id: Long) : ListUiEvent()
    object OnRedirectHandled : ListUiEvent()
}