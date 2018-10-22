package com.arkivanov.mvidroid.sample.app.database.todo

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.arkivanov.mvidroid.sample.app.database.DatabaseOpenHelper
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabaseContract.Column
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabaseContract.Companion.TABLE_NAME
import com.arkivanov.mvidroid.sample.app.model.TodoEntry
import com.arkivanov.mvidroid.sample.app.model.TodoEntryUpdate
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class TodoDatabaseImpl(
    private val helper: DatabaseOpenHelper
) : TodoDatabase {

    private val database: SQLiteDatabase by lazy { helper.writableDatabase }

    private val updatesRelay = PublishRelay.create<TodoEntryUpdate>()
    override val updates: Observable<TodoEntryUpdate> = updatesRelay

    override fun load(): List<TodoEntry> =
        database
            .query(TABLE_NAME, null, null, null, null, null, null)
            .use { cursor ->
                if (cursor.count == 0) {
                    emptyList()
                } else {
                    ArrayList<TodoEntry>(cursor.count).apply {
                        while (cursor.moveToNext()) {
                            add(cursor.toItem())
                        }
                    }
                }
            }

    override fun get(id: Long): TodoEntry? =
        database
            .query(TABLE_NAME, null, "${Column._id}=?", arrayOf(id.toString()), null, null, null)
            .use { if (it.moveToFirst()) it.toItem() else null }

    override fun put(item: TodoEntry): TodoEntry =
        database.run {
            if (item.id == 0L) {
                insert(TABLE_NAME, null, item.toContentValues())
                    .let { item.copy(id = it) }
                    .also { updatesRelay.accept(TodoEntryUpdate.Added(it)) }
            } else {
                update(TABLE_NAME, item.toContentValues(), "${Column._id}=?", arrayOf(item.id.toString()))
                updatesRelay.accept(TodoEntryUpdate.Changed(item))
                item
            }
        }

    override fun delete(id: Long) {
        helper.writableDatabase.delete(TABLE_NAME, "${Column._id}=?", arrayOf(id.toString()))
        updatesRelay.accept(TodoEntryUpdate.Deleted(id))
    }

    override fun <T> transaction(block: TodoDatabase.() -> T): T {
        database.beginTransaction()
        try {
            return this.block().also { database.setTransactionSuccessful() }
        } finally {
            database.endTransaction()
        }
    }

    private companion object {
        private fun Cursor.toItem(): TodoEntry =
            TodoEntry(
                id = getLong(Column._id.ordinal),
                isCompleted = getInt(Column.isCompleted.ordinal) != 0,
                text = getString(Column.text.ordinal)
            )

        private fun TodoEntry.toContentValues(values: ContentValues = ContentValues()): ContentValues =
            values.apply {
                id.takeIf { it > 0 }?.let { put(Column._id.name, it) }
                put(Column.isCompleted.name, if (isCompleted) 1 else 0)
                put(Column.text.name, text)
            }

    }
}
