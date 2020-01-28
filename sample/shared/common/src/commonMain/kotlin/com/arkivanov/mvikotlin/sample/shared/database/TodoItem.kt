package com.arkivanov.mvikotlin.sample.shared.database

import com.arkivanov.mvikotlin.core.utils.JvmSerializable

data class TodoItem(
    val id: String = "",
    val text: String? = null,
    val isDone: Boolean = false
) : JvmSerializable
