package com.arkivanov.mvidroid.sample.app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabaseContract

class DatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, "Todo", null, 1) {

    private companion object : TodoDatabaseContract

    override fun onCreate(db: SQLiteDatabase) {
        createTodoTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}
