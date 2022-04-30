package com.arkivanov.mvikotlin.sample.reaktive.shared.main

import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Model
import com.arkivanov.mvikotlin.sample.reaktive.shared.TestMviView

class TestMainView : TestMviView<Model, Event>(), MainView
