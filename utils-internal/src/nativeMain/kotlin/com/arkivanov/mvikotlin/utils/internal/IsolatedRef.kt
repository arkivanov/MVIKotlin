package com.arkivanov.mvikotlin.utils.internal

@Suppress("ACTUAL_TYPE_ALIAS_TO_CLASS_WITH_DECLARATION_SITE_VARIANCE")
actual typealias IsolatedRef<T> = kotlin.native.concurrent.WorkerBoundReference<T>
