package com.arkivanov.mvidroid.sample.datasource.todo

import com.arkivanov.mvidroid.sample.database.todo.TodoDatabase
import com.arkivanov.mvidroid.sample.model.TodoItem
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TodoDataSourceImpl @Inject constructor(
    private val database: TodoDatabase
) : TodoDataSource {

    override fun load(): Single<List<TodoItem>> =
        Single
            .fromCallable { database.load() }
            .subscribeOn(Schedulers.io())

    override fun get(id: Long): Maybe<TodoItem> =
        Maybe
            .fromCallable<TodoItem> { database.get(id) }
            .subscribeOn(Schedulers.io())

    override fun add(text: String): Single<TodoItem> =
        Single
            .fromCallable { database.put(TodoItem(0, false, text)) }
            .subscribeOn(Schedulers.io())

    override fun setCompleted(id: Long, isCompleted: Boolean): Completable =
        updateItem(id) { copy(isCompleted = isCompleted) }

    override fun setText(id: Long, text: String): Completable =
        updateItem(id) { copy(text = text) }

    override fun delete(id: Long): Completable =
        Completable
            .fromAction { database.delete(id) }
            .subscribeOn(Schedulers.io())

    private inline fun updateItem(id: Long, crossinline func: TodoItem.() -> TodoItem): Completable =
        Completable
            .fromAction {
                database.transaction {
                    get(id)?.func()?.let(::put)
                }
            }
            .subscribeOn(Schedulers.io())
}
