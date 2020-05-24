package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Model

class TestAddView : TestMviView<Model, Event>(), TodoAddView
