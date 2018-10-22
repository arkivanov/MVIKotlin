package com.arkivanov.mvidroid.sample.app.screen.list

import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabase
import com.arkivanov.mvidroid.sample.app.database.todo.updateItem
import com.arkivanov.mvidroid.sample.app.model.TodoEntry
import com.arkivanov.mvidroid.sample.app.model.TodoEntryUpdate
import com.arkivanov.mvidroid.sample.list.dependency.ListDataSource
import com.arkivanov.mvidroid.sample.list.dependency.ListDataSource.Update
import com.arkivanov.mvidroid.sample.list.model.TodoItem
import com.arkivanov.mvidroid.utils.mapNotNull
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ListDataSourceImpl(
    private val database: TodoDatabase
) : ListDataSource {

    override val items: Single<List<TodoItem>> =
        Single
            .fromCallable { database.load() }
            .subscribeOn(Schedulers.io())
            .map { items ->
                items.map { it.toTodoItem() }
            }

    override val updates: Observable<Update> =
        database.updates.mapNotNull {
            when (it) {
                is TodoEntryUpdate.Added -> Update.Added(it.entry.toTodoItem())
                is TodoEntryUpdate.Changed -> Update.Changed(it.entry.toTodoItem())
                is TodoEntryUpdate.Deleted -> Update.Deleted(it.id)
            }
        }

    override fun add(text: String): Single<TodoItem> =
        Single
            .fromCallable { database.put(TodoEntry(text = text)) }
            .subscribeOn(Schedulers.io())
            .map { it.toTodoItem() }

    override fun setCompleted(itemId: Long, isCompleted: Boolean): Completable =
        Completable
            .fromAction {
                database.updateItem(itemId) { it.copy(isCompleted = isCompleted) }
            }
            .subscribeOn(Schedulers.io())

    override fun delete(itemId: Long): Completable =
        Completable
            .fromAction { database.delete(itemId) }
            .subscribeOn(Schedulers.io())

    private companion object {
        private fun TodoEntry.toTodoItem(): TodoItem =
            TodoItem(
                id = id,
                text = text,
                isCompleted = isCompleted
            )
    }
}