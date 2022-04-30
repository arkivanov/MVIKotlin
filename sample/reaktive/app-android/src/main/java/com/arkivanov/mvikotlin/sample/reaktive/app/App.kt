package com.arkivanov.mvikotlin.sample.reaktive.app

import android.app.Application
import com.arkivanov.mvikotlin.sample.database.DefaultTodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.timetravel.server.TimeTravelServer

class App : Application() {

    lateinit var database: TodoDatabase
        private set

    private val timeTravelServer = TimeTravelServer()

    override fun onCreate() {
        super.onCreate()

        database = DefaultTodoDatabase(context = this)
        timeTravelServer.start()
    }
}
