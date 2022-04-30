package com.arkivanov.mvikotlin.sample.database

import com.arkivanov.mvikotlin.core.utils.JvmSerializable

data class TodoItem(
    val id: String,
    val data: Data
) : JvmSerializable {

    data class Data(
        val text: String,
        val isDone: Boolean = false
    ) : JvmSerializable
}
