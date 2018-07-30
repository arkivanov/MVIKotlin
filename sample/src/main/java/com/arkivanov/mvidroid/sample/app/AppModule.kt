package com.arkivanov.mvidroid.sample.app

import android.content.Context
import com.arkivanov.mvidroid.sample.component.Labels
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabase
import com.arkivanov.mvidroid.sample.database.todo.TodoDatabaseImpl
import com.arkivanov.mvidroid.sample.datasource.todo.TodoDataSource
import com.arkivanov.mvidroid.sample.datasource.todo.TodoDataSourceImpl
import com.arkivanov.mvidroid.store.factory.MviDefaultStoreFactory
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import toothpick.config.Module

class AppModule(context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)
        bind(DatabaseOpenHelper::class.java).singletonInScope()
        bind(TodoDatabase::class.java).to(TodoDatabaseImpl::class.java).singletonInScope()
        bind(TodoDataSource::class.java).to(TodoDataSourceImpl::class.java).singletonInScope()
        bind(MviStoreFactory::class.java).toInstance(MviDefaultStoreFactory)
        bind(Relay::class.java).withName(Labels::class.java).toProviderInstance { PublishRelay.create<Any>() }.providesSingletonInScope()
    }
}
