package com.arkivanov.mvidroid.sample.app.database.todo

import com.arkivanov.mvidroid.sample.app.model.TodoEntry
import com.arkivanov.mvidroid.sample.app.model.TodoEntryUpdate
import io.reactivex.Observable

interface TodoDatabase {

    val updates: Observable<TodoEntryUpdate>

    fun load(): List<TodoEntry>

    fun get(id: Long): TodoEntry?

    fun put(item: TodoEntry): TodoEntry

    fun delete(id: Long)

    fun <T> transaction(block: TodoDatabase.() -> T): T
}
