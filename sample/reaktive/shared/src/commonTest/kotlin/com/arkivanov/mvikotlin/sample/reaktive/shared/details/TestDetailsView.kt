package com.arkivanov.mvikotlin.sample.reaktive.shared.details

import com.arkivanov.mvikotlin.sample.reaktive.shared.TestMviView
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.DetailsView.Event
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.DetailsView.Model

class TestDetailsView : TestMviView<Model, Event>(), DetailsView
