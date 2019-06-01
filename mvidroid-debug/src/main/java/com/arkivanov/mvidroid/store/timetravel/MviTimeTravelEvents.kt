package com.arkivanov.mvidroid.store.timetravel

import java.io.Serializable

data class MviTimeTravelEvents(
    val items: List<MviTimeTravelEvent> = emptyList(),
    val index: Int = -1
) : Serializable {

    private companion object {
        private const val serialVersionUID = 1L
    }
}