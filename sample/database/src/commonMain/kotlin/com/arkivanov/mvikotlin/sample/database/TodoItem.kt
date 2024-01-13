package com.arkivanov.mvikotlin.sample.database

import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val id: String,
    val data: Data
) {

    @Serializable
    data class Data(
        val text: String,
        val isDone: Boolean = false
    )
}
