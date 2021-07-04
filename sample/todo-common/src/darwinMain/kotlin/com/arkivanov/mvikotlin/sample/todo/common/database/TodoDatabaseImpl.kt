package com.arkivanov.mvikotlin.sample.todo.common.database

import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem.Data
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getAndUpdate
import platform.Foundation.NSUUID

class TodoDatabaseImpl : TodoDatabase {

    private val map = atomic<Map<String, TodoItem>>(emptyMap())

    init {
        create(Data(text = "Item 1"))
        create(Data(text = "Item 2", isDone = true))
        create(Data(text = "Item 3"))
    }

    override fun get(id: String): TodoItem? = map.value[id]

    override fun create(data: Data): TodoItem {
        val item = TodoItem(id = NSUUID.UUID().UUIDString, data = data)
        map.getAndUpdate { it + (item.id to item) }

        return item
    }

    override fun save(id: String, data: Data) {
        map.getAndUpdate {
            it + (id to requireNotNull(it[id]).copy(data = data))
        }
    }

    override fun delete(id: String) {
        map.getAndUpdate { it - id }
    }

    override fun getAll(): List<TodoItem> = map.value.values.toList()
}
