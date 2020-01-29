package com.arkivanov.mvikotlin.sample.todo.common.database

import android.content.ContentValues
import android.database.Cursor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.COLUMN_ID
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.COLUMN_IS_DONE
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.COLUMN_TEXT
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.INDEX_ID
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.INDEX_IS_DONE
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.INDEX_TEXT

internal object TodoItemContract {
    const val TABLE_NAME = "todo_item"
    const val COLUMN_ID = "id"
    const val COLUMN_TEXT = "text"
    const val COLUMN_IS_DONE = "is_done"
    const val INDEX_ID = 0
    const val INDEX_TEXT = 1
    const val INDEX_IS_DONE = 2
}

internal fun Cursor.getTodoItem(): TodoItem =
    TodoItem(
        id = getString(INDEX_ID),
        data = TodoItem.Data(
            text = getString(INDEX_TEXT),
            isDone = getInt(INDEX_IS_DONE) != 0
        )
    )

internal fun TodoItem.Data.toContentValues(values: ContentValues = ContentValues()): ContentValues {
    values.put(COLUMN_TEXT, text)
    values.put(COLUMN_IS_DONE, isDone)

    return values
}

internal fun TodoItem.toContentValues(values: ContentValues = ContentValues()): ContentValues {
    values.put(COLUMN_ID, id)
    data.toContentValues(values)

    return values
}
