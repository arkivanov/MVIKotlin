package com.arkivanov.mvikotlin.sample.todo.common.database

interface TodoDatabase {

    fun get(id: String): TodoItem?

    fun create(data: TodoItem.Data): TodoItem

    fun save(id: String, data: TodoItem.Data)

    fun delete(id: String)

    fun getAll(): List<TodoItem>
}
