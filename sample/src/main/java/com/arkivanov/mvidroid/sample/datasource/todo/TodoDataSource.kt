package com.arkivanov.mvidroid.sample.datasource.todo

import com.arkivanov.mvidroid.sample.model.TodoItem
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface TodoDataSource {

    fun load(): Single<List<TodoItem>>

    fun get(id: Long): Maybe<TodoItem>

    fun add(text: String): Single<TodoItem>

    fun setCompleted(id: Long, isCompleted: Boolean): Completable

    fun setText(id: Long, text: String): Completable

    fun delete(id: Long): Completable
}
