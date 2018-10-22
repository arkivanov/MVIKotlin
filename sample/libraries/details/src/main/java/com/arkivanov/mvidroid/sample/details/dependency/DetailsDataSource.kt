package com.arkivanov.mvidroid.sample.details.dependency

import com.arkivanov.mvidroid.sample.details.model.TodoDetails
import io.reactivex.Completable
import io.reactivex.Maybe

interface DetailsDataSource {

    fun load(itemId: Long): Maybe<TodoDetails>

    fun setText(itemId: Long, text: String): Completable

    fun setCompleted(itemId: Long, isCompleted: Boolean): Completable

    fun delete(itemId: Long): Completable
}