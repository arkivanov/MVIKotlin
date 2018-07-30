package com.arkivanov.mvidroid.sample.component.list

import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.sample.component.createComponent

interface ListComponent : MviComponent<ListUiEvent, ListStates> {

    companion object {
        fun create(): ListComponent = createComponent<ListComponentImpl>()
    }
}
