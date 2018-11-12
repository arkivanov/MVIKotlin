package com.arkivanov.mvidroid.sample.app.screen.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arkivanov.mvidroid.bind.addViewBundles
import com.arkivanov.mvidroid.bind.attachTo
import com.arkivanov.mvidroid.bind.binder
import com.arkivanov.mvidroid.sample.app.app.app
import com.arkivanov.mvidroid.sample.app.screen.inflateViewWithDebugDrawer
import com.arkivanov.mvidroid.sample.app.screen.router
import com.arkivanov.mvidroid.sample.app.store.storeFactory
import com.arkivanov.mvidroid.sample.details.component.DetailsComponent
import com.arkivanov.mvidroid.sample.details.component.DetailsComponentFactory
import com.arkivanov.mvidroid.sample.details.model.DetailsRedirect
import com.arkivanov.mvidroid.sample.details.ui.DetailsViewFactory

class DetailsFragment : Fragment() {

    private lateinit var component: DetailsComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        val itemId = arguments!!.getLong(EXTRA_ITEM_ID)
        component = DetailsComponentFactory(itemId, storeFactory, DetailsDataSourceImpl(app!!.database)).create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflateViewWithDebugDrawer(DetailsViewFactory.LAYOUT_ID, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binder(component)
            .setDisposeComponent(false)
            .addViewBundles(DetailsViewFactory(view, RedirectHandlerImpl()).create())
            .bind()
            .attachTo(viewLifecycleOwner)
    }

    override fun onDestroy() {
        component.dispose()

        super.onDestroy()
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