package com.arkivanov.mvidroid.sample.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.arkivanov.mvidroid.bind.bind
import com.arkivanov.mvidroid.bind.using
import com.arkivanov.mvidroid.sample.R
import com.arkivanov.mvidroid.sample.component.details.DetailsComponent

class DetailsActivity : AppCompatActivity() {

    private lateinit var view: DetailsView

    companion object {
        const val EXTRA_ITEM_ID = "ITEM_ID"

        @JvmStatic
        fun createIntent(context: Context, itemId: Long): Intent =
            Intent(context, DetailsActivity::class.java).apply {
                putExtra(EXTRA_ITEM_ID, itemId)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_details)

        view = DetailsView(this)
        bind(
            DetailsComponent.create(intent.getLongExtra(EXTRA_ITEM_ID, 0L)),
            view using DetailsViewModelMapper
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = view.onOptionsItemSelected(item)
}
