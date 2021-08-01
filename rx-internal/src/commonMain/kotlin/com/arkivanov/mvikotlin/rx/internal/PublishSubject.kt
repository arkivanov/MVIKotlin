package com.arkivanov.mvikotlin.rx.internal

interface PublishSubject<T> : Subject<T>

@Suppress("FunctionName")
fun <T> PublishSubject(): PublishSubject<T> = PublishSubjectImpl()

private class PublishSubjectImpl<T> : BaseSubject<T>(), PublishSubject<T>
