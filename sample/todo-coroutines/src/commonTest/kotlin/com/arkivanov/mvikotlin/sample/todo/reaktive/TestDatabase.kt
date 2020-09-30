package com.arkivanov.mvikotlin.sample.todo.reaktive

import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.random.Random

class TestDatabase : TodoDatabase {

    private var map by atomic<Map<String, TodoItem.Data>>(emptyMap())

    override fun get(id: String): TodoItem? =
        map[id]?.let { TodoItem(id = id, data = it) }

    override fun create(data: TodoItem.Data): TodoItem {
        val item = TodoItem(id = Random.nextLong().toString(), data = data)
        this.map += item.id to item.data

        return item
    }

    override fun save(id: String, data: TodoItem.Data) {
        this.map += id to data
    }

    override fun delete(id: String) {
        this.map -= id
    }

    override fun getAll(): List<TodoItem> =
        map.map { TodoItem(id = it.key, data = it.value) }
}
