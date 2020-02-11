package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView

interface TodoListController {

    fun onViewCreated(todoListView: TodoListView, todoAddView: TodoAddView)

    fun onStart()

    fun onStop()

    fun onViewDestroyed()

    fun onDestroy()
}
