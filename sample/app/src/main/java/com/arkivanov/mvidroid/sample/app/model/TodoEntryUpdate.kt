package com.arkivanov.mvidroid.sample.app.model

sealed class TodoEntryUpdate {

    class Added(val entry: TodoEntry) : TodoEntryUpdate()
    class Changed(val entry: TodoEntry) : TodoEntryUpdate()
    class Deleted(val id: Long) : TodoEntryUpdate()
}