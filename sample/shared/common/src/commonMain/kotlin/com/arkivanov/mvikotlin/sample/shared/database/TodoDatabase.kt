package com.arkivanov.mvikotlin.sample.shared.database

interface TodoDatabase {

    fun get(id: String): TodoItem?

    fun put(item: TodoItem): TodoItem

    fun delete(id: String)

    fun getAll(): List<TodoItem>
}
