package com.arkivanov.mvikotlin.core.rx

interface Disposable {

    val isDisposed: Boolean

    fun dispose()
}
