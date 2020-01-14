package com.arkivanov.mvikotlin.core.rx

interface Observer<in T> {

    fun onNext(value: T)

    fun onComplete()
}
