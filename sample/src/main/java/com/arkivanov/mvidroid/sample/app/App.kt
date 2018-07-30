package com.arkivanov.mvidroid.sample.app

import android.app.Application
import com.arkivanov.mvidroid.sample.component.app.AppComponent
import toothpick.Scope
import toothpick.Toothpick

class App : Application() {

    lateinit var scope: Scope
        private set

    private lateinit var appComponent: AppComponent

    companion object {
        const val SCOPE_NAME = "APP_SCOPE"
    }

    override fun onCreate() {
        super.onCreate()

        scope = Toothpick.openScopes(SCOPE_NAME)
        scope.installModules(AppModule(this))

        appComponent = AppComponent.create(scope)
    }

    override fun onTerminate() {
        appComponent.dispose()
        Toothpick.closeScope(SCOPE_NAME)

        super.onTerminate()
    }
}
