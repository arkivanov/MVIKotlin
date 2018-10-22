package com.arkivanov.mvidroid.sample.app.model

data class TodoEntry(
    val id: Long = 0L,
    val text: String,
    val isCompleted: Boolean = false
)