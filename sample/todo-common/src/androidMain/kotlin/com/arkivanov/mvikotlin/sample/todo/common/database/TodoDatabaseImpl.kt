package com.arkivanov.mvikotlin.sample.todo.common.database

import android.content.Context
import android.database.Cursor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem.Data
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.COLUMN_ID
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.TABLE_NAME
import java.util.UUID

class TodoDatabaseImpl(
    context: Context
) : TodoDatabase {

    private val database by lazy { TodoDatabaseOpenHelper(context).writableDatabase }

    override fun get(id: String): TodoItem? =
        database
            .queryBy(table = TABLE_NAME, selection = "id=?", selectionArgs = arrayOf(id))
            .use { cursor ->
                cursor
                    .takeIf(Cursor::moveToFirst)
                    ?.getTodoItem()
            }

    override fun create(data: Data): TodoItem {
        val item = TodoItem(id = UUID.randomUUID().toString(), data = data)
        database.insert(TABLE_NAME, null, item.toContentValues())

        return item
    }

    override fun save(id: String, data: Data) {
        database.update(TABLE_NAME, data.toContentValues(), "$COLUMN_ID=?", arrayOf(id))
    }

    override fun delete(id: String) {
        database.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id))
    }

    override fun getAll(): List<TodoItem> =
        database
            .queryBy(table = TABLE_NAME)
            .use { cursor ->
                val list = ArrayList<TodoItem>(cursor.count)
                while (cursor.moveToNext()) {
                    list += cursor.getTodoItem()
                }
                list
            }
}
