package com.arkivanov.mvikotlin.sample.todo.common.database

import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem.Data
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import platform.Foundation.NSUUID

class TodoDatabaseImpl : TodoDatabase {

    private val map = AtomicReference<Map<String, TodoItem>>(emptyMap())

    init {
        create(Data(text = "Item 1"))
        create(Data(text = "Item 2", isDone = true))
        create(Data(text = "Item 3"))
    }

    override fun get(id: String): TodoItem? = map.value[id]

    override fun create(data: Data): TodoItem {
        val item = TodoItem(id = NSUUID.UUID().UUIDString, data = data)
        map.update { it.plus(item.id to item) }

        return item
    }

    override fun save(id: String, data: Data) {
        map.update {
            it.plus(id to requireNotNull(it[id]).copy(data = data))
        }
    }

    override fun delete(id: String) {
        map.update { it.minus(id) }
    }

    override fun getAll(): List<TodoItem> = map.value.values.toList()
}
