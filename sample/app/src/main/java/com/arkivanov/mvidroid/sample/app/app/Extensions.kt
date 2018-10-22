package com.arkivanov.mvidroid.sample.app.app

import android.content.Context
import android.support.v4.app.Fragment

val Context.app: App get() = applicationContext as App

val Fragment.app: App? get() = context?.app