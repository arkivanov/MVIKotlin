package com.arkivanov.mvikotlin.sample.reaktive.shared.main

import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Model
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.AddStore
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.ListStore

internal val statesToModel: (ListStore.State, AddStore.State) -> Model =
    { listState, addState ->
        Model(
            items = listState.items.map(TodoItem::toModelItem),
            text = addState.text,
        )
    }

private fun TodoItem.toModelItem(): Model.Item =
    Model.Item(
        id = id,
        text = data.text,
        isDone = data.isDone,
    )

internal val addLabelToListIntent: (AddStore.Label) -> ListStore.Intent? =
    { label ->
        when (label) {
            is AddStore.Label.Added -> ListStore.Intent.AddToState(item = label.item)
        }
    }

internal val eventToListIntent: (Event) -> ListStore.Intent? =
    { event ->
        when (event) {
            is Event.ItemDeleteClicked -> ListStore.Intent.Delete(id = event.id)
            is Event.ItemDoneClicked -> ListStore.Intent.ToggleDone(id = event.id)
            is Event.ItemClicked,
            is Event.AddClicked,
            is Event.TextChanged -> null
        }
    }


internal val eventToAddIntent: (Event) -> AddStore.Intent? =
    { event ->
        when (event) {
            is Event.TextChanged -> AddStore.Intent.SetText(text = event.text)
            is Event.AddClicked -> AddStore.Intent.Add
            is Event.ItemClicked,
            is Event.ItemDeleteClicked,
            is Event.ItemDoneClicked -> null
        }
    }
