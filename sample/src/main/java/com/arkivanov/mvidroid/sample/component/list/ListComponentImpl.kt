package com.arkivanov.mvidroid.sample.component.list

import com.arkivanov.mvidroid.component.MviAbstractComponent
import com.arkivanov.mvidroid.component.MviStoreBundle
import com.arkivanov.mvidroid.sample.component.Labels
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore
import com.arkivanov.mvidroid.sample.store.todolist.TodoListStore
import com.jakewharton.rxrelay2.Relay
import javax.inject.Inject

class ListComponentImpl @Inject constructor(
    @Labels labels: Relay<Any>,
    todoListStore: TodoListStore,
    todoActionStore: TodoActionStore
) : MviAbstractComponent<ListUiEvent, ListStates, Relay<Any>>(
    stores = listOf(
        MviStoreBundle(
            store = todoListStore,
            uiEventTransformer = TodoListStoreUiEventTransformer,
            isPersistent = true
        ),
        MviStoreBundle(
            store = todoActionStore,
            uiEventTransformer = TodoActionStoreUiEventTransformer,
            isPersistent = true
        )
    ),
    labels = labels
), ListComponent {

    override val states: ListStates = ListStates(todoListStore.states, todoActionStore.states)

    private object TodoListStoreUiEventTransformer : (ListUiEvent) -> TodoListStore.Intent? {
        override fun invoke(event: ListUiEvent): TodoListStore.Intent? =
            when (event) {
                is ListUiEvent.OnAddItem -> TodoListStore.Intent.AddItem(event.text)
                else -> null
            }
    }

    private object TodoActionStoreUiEventTransformer : (ListUiEvent) -> TodoActionStore.Intent? {
        override fun invoke(event: ListUiEvent): TodoActionStore.Intent? =
            when (event) {
                is ListUiEvent.OnItemClick -> TodoActionStore.Intent.ItemSelected(event.id)
                ListUiEvent.OnRedirectedToItemDetails -> TodoActionStore.Intent.HandleRedirectedToDetails
                is ListUiEvent.OnItemCompletedChanged -> TodoActionStore.Intent.SetCompleted(event.id, event.isCompleted)
                is ListUiEvent.OnDeleteItem -> TodoActionStore.Intent.Delete(event.id)
                else -> null
            }
    }
}
