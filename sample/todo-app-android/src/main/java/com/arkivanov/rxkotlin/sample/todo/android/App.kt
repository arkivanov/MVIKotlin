package com.arkivanov.rxkotlin.sample.todo.android

import android.app.Application
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabaseImpl

class App : Application() {

    lateinit var database: TodoDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        database = TodoDatabaseImpl(this)
    }
}
