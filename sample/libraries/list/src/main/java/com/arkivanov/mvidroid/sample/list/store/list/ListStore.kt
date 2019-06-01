package com.arkivanov.mvidroid.sample.list.store.list

import com.arkivanov.mvidroid.sample.list.store.list.ListStore.Intent
import com.arkivanov.mvidroid.store.MviStore
import java.io.Serializable

internal interface ListStore : MviStore<ListState, Intent, Nothing> {

    sealed class Intent : Serializable {
        class Add(val text: String) : Intent()
        class SetCompleted(val itemId: Long, val isCompleted: Boolean) : Intent()
        class Delete(val itemId: Long) : Intent()
    }
}