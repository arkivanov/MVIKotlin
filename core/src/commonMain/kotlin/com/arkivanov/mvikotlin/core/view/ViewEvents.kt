package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer

interface ViewEvents<out Event> {

    @MainThread
    fun events(observer: Observer<Event>): Disposable
}
