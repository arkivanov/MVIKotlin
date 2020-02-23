package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread

interface ViewRenderer<in Model : Any> {

    @MainThread
    fun render(model: Model)
}
