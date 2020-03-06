package com.arkivanov.mvikotlin.sample.todo.common.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.COLUMN_ID
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.COLUMN_IS_DONE
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.COLUMN_TEXT
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItemContract.TABLE_NAME

internal class TodoDatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, "Todo", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
                create table $TABLE_NAME (
                    $COLUMN_ID text primary key,
                    $COLUMN_TEXT text not null,
                    $COLUMN_IS_DONE integer not null
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}
