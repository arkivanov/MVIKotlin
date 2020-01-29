package com.arkivanov.mvikotlin.sample.todo.common.database

import android.content.Context
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem.*
import java.util.UUID

class TodoDatabaseImpl(
    context: Context
) : TodoDatabase {

    private val map = HashMap<String, TodoItem>()

    init {
        create(Data(text = "kek"))
    }

    override fun get(id: String): TodoItem? = map[id]

    override fun create(data: Data): TodoItem {
        val id = UUID.randomUUID().toString()
        val item = TodoItem(id = id, data = data)
        map[id] = item

        return item
    }

    override fun put(id: String, data: Data) {
        val item = map[id] ?: return
        map[id] = item.copy(data = data)
    }

    override fun delete(id: String) {
        map -= id
    }

    override fun getAll(): List<TodoItem> = map.values.toList()
}
