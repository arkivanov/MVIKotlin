package com.arkivanov.mvikotlin.core.view

/**
 * Represents a `View`, a combination of the [ViewRenderer] and the [ViewEvents].
 */
interface MviView<in Model : Any, out Event : Any> : ViewRenderer<Model>, ViewEvents<Event>
