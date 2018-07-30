package com.arkivanov.mvidroid.sample.component.list

sealed class ListUiEvent {

    class OnAddItem(val text: String) : ListUiEvent()
    class OnItemClick(val id: Long) : ListUiEvent()
    object OnRedirectedToItemDetails : ListUiEvent()
    class OnItemCompletedChanged(val id: Long, val isCompleted: Boolean) : ListUiEvent()
    class OnDeleteItem(val id: Long) : ListUiEvent()
}
