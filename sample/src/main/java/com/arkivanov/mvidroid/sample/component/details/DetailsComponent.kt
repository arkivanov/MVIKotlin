package com.arkivanov.mvidroid.sample.component.details

import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.sample.component.createComponent
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsState
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsStore
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsStoreParams
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsStoreProvider
import io.reactivex.Observable

interface DetailsComponent : MviComponent<DetailsUiEvent, Observable<TodoDetailsState>> {

    companion object {
        fun create(itemId: Long): DetailsComponent =
            createComponent<DetailsComponentImpl> {
                bind(TodoDetailsStoreParams::class.java).toInstance(TodoDetailsStoreParams(itemId))

                bind(TodoDetailsStore::class.java)
                    .toProvider(TodoDetailsStoreProvider::class.java)
                    .providesSingletonInScope()

                bind(DetailsComponentParams::class.java).toInstance(DetailsComponentParams(itemId))
            }
    }
}
