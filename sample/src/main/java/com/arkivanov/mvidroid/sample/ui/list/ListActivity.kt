package com.arkivanov.mvidroid.sample.ui.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arkivanov.mvidroid.bind.bind
import com.arkivanov.mvidroid.bind.using
import com.arkivanov.mvidroid.sample.R
import com.arkivanov.mvidroid.sample.component.list.ListComponent

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bind(
            ListComponent.create(),
            ListView(this) using ListViewModelMapper
        )
    }
}
