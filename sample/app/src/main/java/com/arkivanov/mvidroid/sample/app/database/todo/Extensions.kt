package com.arkivanov.mvidroid.sample.app.database.todo

import com.arkivanov.mvidroid.sample.app.model.TodoEntry

fun TodoDatabase.updateItem(id: Long, transform: (TodoEntry) -> TodoEntry): TodoEntry? = transaction { get(id)?.let(transform)?.let(::put) }