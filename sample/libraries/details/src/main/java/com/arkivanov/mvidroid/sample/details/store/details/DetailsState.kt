package com.arkivanov.mvidroid.sample.details.store.details

import com.arkivanov.mvidroid.sample.details.model.TodoDetails
import java.io.Serializable

data class DetailsState(
    val details: TodoDetails? = null,
    val isLoadingError: Boolean = false
) : Serializable
