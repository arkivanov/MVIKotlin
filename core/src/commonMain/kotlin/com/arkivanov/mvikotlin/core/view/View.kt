package com.arkivanov.mvikotlin.core.view

interface View<in Model, out Event> : ViewRenderer<Model>, ViewEvents<Event>
