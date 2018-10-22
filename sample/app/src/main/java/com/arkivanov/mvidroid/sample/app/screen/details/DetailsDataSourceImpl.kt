package com.arkivanov.mvidroid.sample.app.screen.details

import com.arkivanov.mvidroid.sample.app.database.todo.TodoDatabase
import com.arkivanov.mvidroid.sample.app.database.todo.updateItem
import com.arkivanov.mvidroid.sample.app.model.TodoEntry
import com.arkivanov.mvidroid.sample.details.dependency.DetailsDataSource
import com.arkivanov.mvidroid.sample.details.model.TodoDetails
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

class DetailsDataSourceImpl(
    private val database: TodoDatabase
) : DetailsDataSource {

    override fun load(itemId: Long): Maybe<TodoDetails> =
        Maybe
            .fromCallable { database.get(itemId) }
            .subscribeOn(Schedulers.io())
            .map { it.toDetails() }

    override fun setText(itemId: Long, text: String): Completable =
        Completable
            .fromAction {
                database.updateItem(itemId) { it.copy(text = text) }
            }
            .subscribeOn(Schedulers.io())

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
        private fun TodoEntry.toDetails(): TodoDetails =
            TodoDetails(
                text = text,
                isCompleted = isCompleted
            )
    }
}