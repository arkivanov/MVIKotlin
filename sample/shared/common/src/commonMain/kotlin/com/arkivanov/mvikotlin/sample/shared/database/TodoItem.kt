package com.arkivanov.mvikotlin.sample.shared.database

data class TodoItem(
    val id: String = "",
    val text: String? = null,
    val isDone: Boolean = false
)
