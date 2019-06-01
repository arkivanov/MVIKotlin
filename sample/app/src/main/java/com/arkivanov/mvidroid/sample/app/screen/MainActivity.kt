package com.arkivanov.mvidroid.sample.app.screen

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.arkivanov.mvidroid.sample.app.BuildConfig
import com.arkivanov.mvidroid.sample.app.R
import com.arkivanov.mvidroid.sample.app.screen.list.ListFragment

class MainActivity : AppCompatActivity(), Router {

    @IdRes
    private val contentId: Int = if (BuildConfig.DEBUG) R.id.content else android.R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            setContentView(R.layout.main_activity_debug)
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(contentId, ListFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun startFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(contentId, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun finishFragment() {
        supportFragmentManager.popBackStack()
    }
}