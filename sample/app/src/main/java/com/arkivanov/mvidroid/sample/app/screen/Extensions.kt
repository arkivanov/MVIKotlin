package com.arkivanov.mvidroid.sample.app.screen

import android.support.v4.app.Fragment

val Fragment.router: Router? get() = activity as? Router
