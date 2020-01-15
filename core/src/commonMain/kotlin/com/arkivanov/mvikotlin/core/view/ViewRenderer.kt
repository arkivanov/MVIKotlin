package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread

interface ViewRenderer<in Model> {

    @MainThread
    fun render(model: Model)
}
