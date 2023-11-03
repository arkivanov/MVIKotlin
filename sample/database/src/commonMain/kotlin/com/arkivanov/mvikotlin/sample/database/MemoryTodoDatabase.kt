package com.arkivanov.mvikotlin.sample.database

import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getAndUpdate

class MemoryTodoDatabase : TodoDatabase {

    private var idCounter = atomic(1L)
    private val map = atomic<Map<String, TodoItem.Data>>(emptyMap())

    override fun get(id: String): TodoItem? =
        map.value[id]?.let { TodoItem(id = id, data = it) }

    override fun create(data: TodoItem.Data): TodoItem {
        val id = idCounter.getAndUpdate { it + 1 }.toString()
        val item = TodoItem(id = id, data = data)
        map.getAndUpdate { it + (item.id to item.data) }

        return item
    }

    override fun save(id: String, data: TodoItem.Data) {
        map.getAndUpdate { it + (id to data) }
    }

    override fun delete(id: String) {
        map.getAndUpdate { it - id }
    }

    override fun getAll(): List<TodoItem> =
        map.value.map { TodoItem(id = it.key, data = it.value) }
}
