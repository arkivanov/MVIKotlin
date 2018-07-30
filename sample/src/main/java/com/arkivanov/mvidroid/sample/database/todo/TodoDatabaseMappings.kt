package com.arkivanov.mvidroid.sample.database.todo

import android.content.ContentValues
import android.database.Cursor
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseContract.Columns
import com.arkivanov.mvidroid.sample.model.TodoItem

interface TodoDatabaseMappings {

    fun Cursor.toTodoItem(): TodoItem =
        TodoItem(
            id = getLong(Columns._id.ordinal),
            isCompleted = getInt(Columns.isCompleted.ordinal) != 0,
            text = getString(Columns.text.ordinal)
        )

    fun TodoItem.toContentValues(values: ContentValues = ContentValues()): ContentValues =
        values.apply {
            id.takeIf { it > 0 }?.let { put(Columns._id.name, it) }
            put(Columns.isCompleted.name, if (isCompleted) 1 else 0)
            put(Columns.text.name, text)
        }
}
