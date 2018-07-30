package com.arkivanov.mvidroid.sample.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseContract
import javax.inject.Inject

class DatabaseOpenHelper @Inject constructor(context: Context) : SQLiteOpenHelper(context, "Todo", null, 1) {

    private companion object : TodoDatabaseContract

    override fun onCreate(db: SQLiteDatabase) {
        createTodoTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}
