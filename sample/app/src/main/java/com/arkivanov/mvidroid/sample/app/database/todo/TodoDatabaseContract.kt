package com.arkivanov.mvidroid.sample.app.database.todo

import android.database.sqlite.SQLiteDatabase
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabaseContract.Column._id
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabaseContract.Column.isCompleted
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabaseContract.Column.text

interface TodoDatabaseContract {

    companion object {
        const val TABLE_NAME = "todo"
    }

    @Suppress("EnumEntryName")
    enum class Column {
        _id, isCompleted, text;

        override fun toString(): String = name
    }

    fun createTodoTable(database: SQLiteDatabase) {
        database.execSQL(
            """
                create table $TABLE_NAME (
                    $_id integer primary key autoincrement,
                    $isCompleted integer not null,
                    $text text not null
                )
            """
        )
    }
}
