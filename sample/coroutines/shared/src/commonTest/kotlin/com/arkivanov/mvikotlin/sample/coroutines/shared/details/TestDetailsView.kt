package com.arkivanov.mvikotlin.sample.coroutines.shared.details

import com.arkivanov.mvikotlin.sample.coroutines.shared.TestMviView
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Event
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Model

class TestDetailsView : TestMviView<Model, Event>(), DetailsView
