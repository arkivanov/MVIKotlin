package com.arkivanov.mvidroid.sample.ui.list

import com.arkivanov.mvidroid.sample.model.TodoItem

data class ListViewModel(
    val items: List<TodoItem>,
    val detailsRedirectItemId: Long?
)
