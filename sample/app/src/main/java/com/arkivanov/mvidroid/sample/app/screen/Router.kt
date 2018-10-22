package com.arkivanov.mvidroid.sample.app.screen

import android.support.v4.app.Fragment

interface Router {

    fun startFragment(fragment: Fragment)

    fun finishFragment()
}