package com.arkivanov.mvidroid.sample.app.screen.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arkivanov.mvidroid.bind.MviBinder
import com.arkivanov.mvidroid.bind.addViewBundles
import com.arkivanov.mvidroid.bind.attachTo
import com.arkivanov.mvidroid.sample.app.app.app
import com.arkivanov.mvidroid.sample.app.screen.details.DetailsFragment
import com.arkivanov.mvidroid.sample.app.screen.inflateViewWithDebugDrawer
import com.arkivanov.mvidroid.sample.app.screen.router
import com.arkivanov.mvidroid.sample.app.store.storeFactory
import com.arkivanov.mvidroid.sample.list.component.ListComponent
import com.arkivanov.mvidroid.sample.list.component.ListComponentFactory
import com.arkivanov.mvidroid.sample.list.model.ListRedirect
import com.arkivanov.mvidroid.sample.list.ui.ListViewFactory

class ListFragment : Fragment() {

    private lateinit var component: ListComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        component = ListComponentFactory(storeFactory, ListDataSourceImpl(app!!.database)).create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflateViewWithDebugDrawer(ListViewFactory.LAYOUT_ID, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MviBinder(component)
            .setDisposeComponent(false)
            .addViewBundles(ListViewFactory(view, RedirectHandlerImpl()).create())
            .bind()
            .attachTo(viewLifecycleOwner)
    }

    override fun onDestroy() {
        component.dispose()

        super.onDestroy()
    }

    private inner class RedirectHandlerImpl : (ListRedirect) -> Unit {
        override fun invoke(redirect: ListRedirect) {
            when (redirect) {
                is ListRedirect.ShowItemDetails -> router?.startFragment(DetailsFragment.newInstance(redirect.itemId))
            }
        }
    }
}
