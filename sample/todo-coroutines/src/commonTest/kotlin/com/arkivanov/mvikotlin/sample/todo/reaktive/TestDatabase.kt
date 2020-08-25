package com.arkivanov.mvikotlin.sample.todo.reaktive

import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import kotlin.random.Random

class TestDatabase : TodoDatabase {

    private val map = AtomicReference<Map<String, TodoItem.Data>>(emptyMap())

    override fun get(id: String): TodoItem? =
        map.value[id]?.let { TodoItem(id = id, data = it) }

    override fun create(data: TodoItem.Data): TodoItem {
        val item = TodoItem(id = Random.nextLong().toString(), data = data)
        map.update { it + (item.id to item.data) }

        return item
    }

    override fun save(id: String, data: TodoItem.Data) {
        map.update {
            it + (id to data)
        }
    }

    override fun delete(id: String) {
        map.update {
            it - id
        }
    }

    override fun getAll(): List<TodoItem> =
        map.value.map { TodoItem(id = it.key, data = it.value) }
}
