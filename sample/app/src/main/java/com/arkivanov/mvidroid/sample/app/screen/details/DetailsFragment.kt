package com.arkivanov.mvidroid.sample.app.screen.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arkivanov.mvidroid.sample.app.app.app
import com.arkivanov.mvidroid.sample.app.screen.inflateViewWithDebugDrawer
import com.arkivanov.mvidroid.sample.app.screen.router
import com.arkivanov.mvidroid.sample.app.store.storeFactory
import com.arkivanov.mvidroid.sample.details.DetailsComponent
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect

class DetailsFragment : Fragment() {

    private lateinit var component: DetailsComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        component =
            DetailsComponent(
                itemId = arguments!!.getLong(EXTRA_ITEM_ID),
                storeFactory = storeFactory,
                detailsDataSource = DetailsDataSourceImpl(app!!.database),
                lifecycle = lifecycle
            )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflateViewWithDebugDrawer(DetailsComponent.LAYOUT_ID, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.bindView(view, viewLifecycleOwner.lifecycle, RedirectHandlerImpl())
    }

    companion object {
        private const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"

        fun newInstance(itemId: Long): DetailsFragment =
            DetailsFragment().apply {
                arguments = createArguments(itemId)
            }

        private fun createArguments(itemId: Long): Bundle =
            Bundle().apply {
                putLong(EXTRA_ITEM_ID, itemId)
            }
    }

    private inner class RedirectHandlerImpl : (DetailsRedirect) -> Unit {
        override fun invoke(redirect: DetailsRedirect) {
            when (redirect) {
                DetailsRedirect.Finish -> router?.finishFragment()
            }
        }
    }
}