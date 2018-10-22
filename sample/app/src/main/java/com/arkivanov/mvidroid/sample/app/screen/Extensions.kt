package com.arkivanov.mvidroid.sample.app.screen

import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arkivanov.mvidroid.sample.app.BuildConfig
import com.arkivanov.mvidroid.widget.MviTimeTravelDrawer

val Fragment.router: Router? get() = activity as? Router

fun LayoutInflater.inflateViewWithDebugDrawer(@LayoutRes layoutId: Int, parent: ViewGroup?): View =
    if (BuildConfig.DEBUG) {
        MviTimeTravelDrawer(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setContentView(inflate(layoutId, this, false))
        }
    } else {
        inflate(layoutId, parent, false)
    }