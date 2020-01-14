package com.arkivanov.mvikotlin.core.view

interface ViewRenderer<in Model> {

    fun render(model: Model)
}
