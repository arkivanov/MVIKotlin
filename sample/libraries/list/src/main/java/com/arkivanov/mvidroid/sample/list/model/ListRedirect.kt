package com.arkivanov.mvidroid.sample.list.model

import java.io.Serializable

sealed class ListRedirect : Serializable {

    class ShowItemDetails(val itemId: Long) : ListRedirect()
}