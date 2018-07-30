package com.arkivanov.mvidroid.sample.database.todo

import com.arkivanov.mvidroid.sample.model.TodoItem

interface TodoDatabase {

    fun load(): List<TodoItem>

    fun get(id: Long): TodoItem?

    fun put(item: TodoItem): TodoItem

    fun delete(id: Long)

    fun <T> transaction(block: TodoDatabase.() -> T): T
}
