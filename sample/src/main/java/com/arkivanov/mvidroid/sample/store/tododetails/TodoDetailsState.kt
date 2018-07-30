package com.arkivanov.mvidroid.sample.store.tododetails

data class TodoDetailsState(
    val text: String? = null,
    val isCompleted: Boolean = false,
    val isFinished: Boolean = false
)
