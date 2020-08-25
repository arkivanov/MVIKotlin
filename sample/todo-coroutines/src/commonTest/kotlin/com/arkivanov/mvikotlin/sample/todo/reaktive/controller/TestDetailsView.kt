package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Model

class TestDetailsView : TestMviView<Model, Event>(), TodoDetailsView
