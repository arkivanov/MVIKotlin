package com.arkivanov.mvidroid.sample.database.todo

import android.database.sqlite.SQLiteDatabase
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseContract.Columns._id
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseContract.Columns.isCompleted
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseContract.Columns.text

interface TodoDatabaseContract {

    companion object {
        const val TABLE_NAME = "todo"
    }

    @Suppress("EnumEntryName")
    enum class Columns {
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
