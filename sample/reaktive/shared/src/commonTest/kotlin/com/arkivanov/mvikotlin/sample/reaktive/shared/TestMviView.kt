package com.arkivanov.mvikotlin.sample.reaktive.shared

import com.arkivanov.mvikotlin.core.view.BaseMviView

open class TestMviView<Model : Any, Event : Any> : BaseMviView<Model, Event>() {

    lateinit var model: Model

    override fun render(model: Model) {
        super.render(model)

        this.model = model
    }
}
