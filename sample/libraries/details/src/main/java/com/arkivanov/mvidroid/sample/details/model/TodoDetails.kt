package com.arkivanov.mvidroid.sample.details.model

import java.io.Serializable

data class TodoDetails(
    val text: String,
    val isCompleted: Boolean
) : Serializable
