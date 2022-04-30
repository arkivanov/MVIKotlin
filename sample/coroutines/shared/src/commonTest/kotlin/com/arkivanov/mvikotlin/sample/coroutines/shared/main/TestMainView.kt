package com.arkivanov.mvikotlin.sample.coroutines.shared.main

import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView.Model
import com.arkivanov.mvikotlin.sample.coroutines.shared.TestMviView

class TestMainView : TestMviView<Model, Event>(), MainView
