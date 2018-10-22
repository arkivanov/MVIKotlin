package com.arkivanov.mvidroid.sample.app.app

import android.app.Application
import com.arkivanov.mvidroid.sample.app.database.DatabaseOpenHelper
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabase
import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabaseImpl

class App : Application() {

    val database: TodoDatabase by lazy { TodoDatabaseImpl(DatabaseOpenHelper(this)) }
}