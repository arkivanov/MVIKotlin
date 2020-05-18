package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread
import kotlin.js.JsName

/**
 * Represents a consumer of the `View Models`
 *
 * @see MviView
 */
interface ViewRenderer<in Model : Any> {

    /**
     * Renders (displays) the provided `View Model`
     *
     * @param model a `View Model` to be rendered (displayed)
     */
    @JsName("render")
    @MainThread
    fun render(model: Model)
}
