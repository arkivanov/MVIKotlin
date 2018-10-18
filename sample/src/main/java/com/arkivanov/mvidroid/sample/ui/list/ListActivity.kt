package com.arkivanov.mvidroid.sample.ui.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arkivanov.mvidroid.bind.Binder
import com.arkivanov.mvidroid.bind.attachTo
import com.arkivanov.mvidroid.sample.R
import com.arkivanov.mvidroid.sample.component.list.ListComponent

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Binder(ListComponent.create())
            .addView(ListView(this), ListViewModelMapper)
            .bind()
            .attachTo(this)
    }
}
