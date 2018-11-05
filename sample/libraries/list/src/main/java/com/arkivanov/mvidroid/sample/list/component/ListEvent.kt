package com.arkivanov.mvidroid.sample.list.component

sealed class ListEvent {

    class OnAddItem(val text: String) : ListEvent()
    class OnSetItemCompleted(val id: Long, val isCompleted: Boolean) : ListEvent()
    class OnDeleteItem(val id: Long) : ListEvent()
    class OnItemSelected(val id: Long) : ListEvent()
    object OnRedirectHandled : ListEvent()
}