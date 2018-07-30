package com.arkivanov.mvidroid.sample.component.app

import com.arkivanov.mvidroid.component.MviAbstractComponent
import com.arkivanov.mvidroid.component.MviStoreBundle
import com.arkivanov.mvidroid.sample.component.Labels
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore
import com.arkivanov.mvidroid.sample.store.todolist.TodoListStore
import com.jakewharton.rxrelay2.Relay
import javax.inject.Inject

class AppComponentImpl @Inject constructor(
    @Labels labels: Relay<Any>,
    todoListStore: TodoListStore,
    todoActionStore: TodoActionStore
) : MviAbstractComponent<Nothing, Nothing>(
    stores = listOf(
        MviStoreBundle(
            store = todoListStore,
            labelTransformer = TodoListStoreLabelTransformer
        ),
        MviStoreBundle(
            store = todoActionStore
        )
    ),
    labels = labels
), AppComponent {

    override val states: Nothing
        get() = throw NotImplementedError()

    private object TodoListStoreLabelTransformer : (Any) -> TodoListStore.Intent? {
        override fun invoke(label: Any): TodoListStore.Intent? =
            when (label) {
                is TodoActionStore.Label.ItemTextChanged -> TodoListStore.Intent.HandleItemTextChanged(label.id, label.text)

                is TodoActionStore.Label.ItemCompletedChanged ->
                    TodoListStore.Intent.HandleItemCompletedChanged(label.id, label.isCompleted)

                is TodoActionStore.Label.ItemDeleted -> TodoListStore.Intent.HandleItemDeleted(label.id)
                else -> null
            }
    }
}
