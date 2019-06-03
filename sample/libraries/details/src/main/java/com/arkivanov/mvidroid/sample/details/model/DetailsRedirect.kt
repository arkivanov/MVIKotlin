package com.arkivanov.mvidroid.sample.details.model

import java.io.Serializable

sealed class DetailsRedirect : Serializable {

    object Finish : DetailsRedirect()
}