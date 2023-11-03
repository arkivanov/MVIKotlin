package com.arkivanov.mvikotlin.rx.internal

interface PublishSubject<T> : Subject<T>

@Suppress("FunctionNaming") // https://github.com/detekt/detekt/issues/6601
fun <T> PublishSubject(): PublishSubject<T> = PublishSubjectImpl()

private class PublishSubjectImpl<T> : BaseSubject<T>(), PublishSubject<T>
