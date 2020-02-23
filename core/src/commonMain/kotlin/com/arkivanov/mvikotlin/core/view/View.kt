package com.arkivanov.mvikotlin.core.view

interface View<in Model: Any, out Event : Any> : ViewRenderer<Model>, ViewEvents<Event>
