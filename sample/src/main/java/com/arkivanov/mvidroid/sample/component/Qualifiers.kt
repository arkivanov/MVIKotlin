package com.arkivanov.mvidroid.sample.component

import javax.inject.Qualifier

@Retention(AnnotationRetention.SOURCE)
@Qualifier
annotation class Labels

@Retention(AnnotationRetention.SOURCE)
@Qualifier
annotation class OnDisposeAction
