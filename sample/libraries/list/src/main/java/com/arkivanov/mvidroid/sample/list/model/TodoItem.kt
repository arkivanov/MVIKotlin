package com.arkivanov.mvidroid.sample.list.model

import java.io.Serializable

data class TodoItem(
    val id: Long,
    val text: String,
    val isCompleted: Boolean
) : Serializable