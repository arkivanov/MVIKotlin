package com.arkivanov.mvidroid.sample.database.todo

import android.database.sqlite.SQLiteDatabase
import com.arkivanov.mvidroid.sample.app.DatabaseOpenHelper
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseContract.Columns._id
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseContract.Companion.TABLE_NAME
import com.arkivanov.mvidroid.sample.model.TodoItem
import javax.inject.Inject

class TodoDatabaseImpl @Inject constructor(
    private val helper: DatabaseOpenHelper
) : TodoDatabase {

    private val database: SQLiteDatabase by lazy { helper.writableDatabase }

    private companion object : TodoDatabaseMappings

    override fun load(): List<TodoItem> =
        database
            .query(TABLE_NAME, null, null, null, null, null, null)
            .use { cursor ->
                if (cursor.count == 0) {
                    emptyList()
                } else {
                    ArrayList<TodoItem>(cursor.count).apply {
                        while (cursor.moveToNext()) {
                            add(cursor.toTodoItem())
                        }
                    }
                }
            }

    override fun get(id: Long): TodoItem? =
        database
            .query(TABLE_NAME, null, "$_id=?", arrayOf(id.toString()), null, null, null)
            .use { if (it.moveToFirst()) it.toTodoItem() else null }

    override fun put(item: TodoItem): TodoItem =
        database.run {
            if (item.id == 0L) {
                item.copy(id = insert(TABLE_NAME, null, item.toContentValues()))
            } else {
                update(TABLE_NAME, item.toContentValues(), "$_id=?", arrayOf(item.id.toString()))
                item
            }
        }

    override fun delete(id: Long) {
        helper.writableDatabase.delete(TABLE_NAME, "$_id=?", arrayOf(id.toString()))
    }

    override fun <T> transaction(block: TodoDatabase.() -> T): T {
        database.beginTransaction()
        try {
            return this.block().also { database.setTransactionSuccessful() }
        } finally {
            database.endTransaction()
        }
    }
}
