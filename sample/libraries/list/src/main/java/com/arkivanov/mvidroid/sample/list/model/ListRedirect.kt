package com.arkivanov.mvidroid.sample.list.model

sealed class ListRedirect {

    class ShowItemDetails(val itemId: Long) : ListRedirect()
}