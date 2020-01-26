package com.arkivanov.rxkotlin.sample.android.app

import android.app.Application
import com.arkivanov.mvikotlin.sample.shared.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.shared.database.TodoDatabaseImpl

class App : Application() {

    lateinit var database: TodoDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        database = TodoDatabaseImpl(this)
    }
}
