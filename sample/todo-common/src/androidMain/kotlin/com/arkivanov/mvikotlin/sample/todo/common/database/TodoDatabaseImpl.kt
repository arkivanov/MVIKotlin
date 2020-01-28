package com.arkivanov.mvikotlin.sample.todo.common.database

import android.content.Context
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class TodoDatabaseImpl(
    context: Context
) : TodoDatabase {

    private val map = ConcurrentHashMap<String, TodoItem>()

    override fun get(id: String): TodoItem? = map[id]

    override fun put(item: TodoItem): TodoItem =
        if (item.id == "") {
            val newItem = item.copy(id = UUID.randomUUID().toString())
            map[newItem.id] = newItem
            newItem
        } else {
            map[item.id] = item
            item
        }

    override fun delete(id: String) {
        map -= id
    }

    override fun getAll(): List<TodoItem> = map.values.toList()
}
