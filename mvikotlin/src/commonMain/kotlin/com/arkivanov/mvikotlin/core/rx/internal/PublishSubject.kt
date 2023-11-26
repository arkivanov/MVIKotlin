package com.arkivanov.mvikotlin.core.rx.internal

import com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi

@InternalMviKotlinApi
interface PublishSubject<T> : Subject<T>

@Suppress("FunctionNaming") // https://github.com/detekt/detekt/issues/6601
@InternalMviKotlinApi
fun <T> PublishSubject(): PublishSubject<T> =
    PublishSubjectImpl()

@OptIn(InternalMviKotlinApi::class)
private class PublishSubjectImpl<T> : BaseSubject<T>(), PublishSubject<T>
