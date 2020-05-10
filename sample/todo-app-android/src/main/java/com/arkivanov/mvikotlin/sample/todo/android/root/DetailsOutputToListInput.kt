package com.arkivanov.mvikotlin.sample.todo.android.root

import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Output
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Input

internal val detailsOutputToListInput: Output.() -> Input? =
    {
        when (this) {
            is Output.Finished -> null
            is Output.ItemChanged -> Input.ItemChanged(id = id, data = data)
            is Output.ItemDeleted -> Input.ItemDeleted(id = id)
        }
    }
