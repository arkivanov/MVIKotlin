package com.arkivanov.mvidroid.sample.details.store.details

import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStore.Intent
import com.arkivanov.mvidroid.sample.details.store.details.DetailsStore.Label
import com.arkivanov.mvidroid.store.MviStore

internal interface DetailsStore : MviStore<DetailsState, Intent, Label> {

    sealed class Intent {
        class SetText(val text: String) : Intent()
        class SetCompleted(val isCompleted: Boolean) : Intent()
        object Delete : Intent()
    }

    sealed class Label {
        class Redirect(val redirect: DetailsRedirect) : Label()
    }
}