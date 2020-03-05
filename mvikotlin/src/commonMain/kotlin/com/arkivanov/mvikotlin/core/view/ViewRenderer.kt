package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread

/**
 * Represents a consumer of the `View Models`
 *
 * @see View
 */
interface ViewRenderer<in Model : Any> {

    /**
     * Renders (displays) the provided `View Model`
     *
     * @param model a `View Model` to be rendered (displayed)
     */
    @MainThread
    fun render(model: Model)
}
