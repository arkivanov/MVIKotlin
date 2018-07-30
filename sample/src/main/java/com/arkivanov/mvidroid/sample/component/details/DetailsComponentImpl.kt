package com.arkivanov.mvidroid.sample.component.details

import com.arkivanov.mvidroid.component.MviAbstractComponent
import com.arkivanov.mvidroid.component.MviStoreBundle
import com.arkivanov.mvidroid.sample.component.Labels
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsState
import com.arkivanov.mvidroid.sample.store.tododetails.TodoDetailsStore
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import javax.inject.Inject

class DetailsComponentImpl @Inject constructor(
    @Labels labels: Relay<Any>,
    params: DetailsComponentParams,
    todoDetailsStore: TodoDetailsStore,
    todoActionStore: TodoActionStore
) : MviAbstractComponent<DetailsUiEvent, Observable<TodoDetailsState>>(
    stores = listOf(
        MviStoreBundle(
            store = todoDetailsStore,
            labelTransformer = TodoDetailsStoreLabelTransformer(params.itemId)
        ),
        MviStoreBundle(
            store = todoActionStore,
            uiEventTransformer = TodoActionStoreUiEventTransformer(params.itemId),
            isPersistent = true
        )
    ),
    labels = labels
), DetailsComponent {

    override val states: Observable<TodoDetailsState> = todoDetailsStore.states

    private class TodoDetailsStoreLabelTransformer(
        private val itemId: Long
    ) : (Any) -> TodoDetailsStore.Intent? {
        override fun invoke(label: Any): TodoDetailsStore.Intent? =
            when (label) {
                is TodoActionStore.Label.ItemTextChanged ->
                    label
                        .takeIf { it.id == itemId }
                        ?.let { TodoDetailsStore.Intent.HandleTextChanged(it.text) }

                is TodoActionStore.Label.ItemCompletedChanged ->
                    label
                        .takeIf { it.id == itemId }
                        ?.let { TodoDetailsStore.Intent.HandleCompletedChanged(it.isCompleted) }

                is TodoActionStore.Label.ItemDeleted ->
                    label
                        .takeIf { it.id == itemId }
                        ?.let { TodoDetailsStore.Intent.HandleDeleted }

                else -> null
            }
    }

    private class TodoActionStoreUiEventTransformer(
        private val itemId: Long
    ) : (DetailsUiEvent) -> TodoActionStore.Intent? {
        override fun invoke(event: DetailsUiEvent): TodoActionStore.Intent? =
            when (event) {
                is DetailsUiEvent.OnTextChanged -> TodoActionStore.Intent.SetText(itemId, event.text)
                is DetailsUiEvent.OnCompletedChanged -> TodoActionStore.Intent.SetCompleted(itemId, event.isCompleted)
                DetailsUiEvent.OnDelete -> TodoActionStore.Intent.Delete(itemId)
            }
    }
}
