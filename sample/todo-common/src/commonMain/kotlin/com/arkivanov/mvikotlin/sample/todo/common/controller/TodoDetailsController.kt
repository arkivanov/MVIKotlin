package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView

interface TodoDetailsController {

    fun onViewCreated(todoDetailsView: TodoDetailsView)

    fun onStart()

    fun onStop()

    fun onViewDestroyed()

    fun onDestroy()
}
