package com.arkivanov.mvidroid.sample.list.dependency

import com.arkivanov.mvidroid.sample.list.model.TodoItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ListDataSource {

    val items: Single<List<TodoItem>>
    val updates: Observable<Update>

    fun add(text: String): Single<TodoItem>

    fun setCompleted(itemId: Long, isCompleted: Boolean): Completable

    fun delete(itemId: Long): Completable

    sealed class Update {
        class Added(val item: TodoItem) : Update()
        class Changed(val item: TodoItem) : Update()
        class Deleted(val itemId: Long) : Update()
    }
}