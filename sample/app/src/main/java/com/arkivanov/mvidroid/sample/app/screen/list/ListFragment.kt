package com.arkivanov.mvidroid.sample.app.screen.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arkivanov.mvidroid.sample.app.app.app
import com.arkivanov.mvidroid.sample.app.screen.details.DetailsFragment
import com.arkivanov.mvidroid.sample.app.screen.router
import com.arkivanov.mvidroid.sample.app.store.storeFactory
import com.arkivanov.mvidroid.sample.list.ListComponent
import com.arkivanov.mvidroid.sample.list.model.ListRedirect

class ListFragment : Fragment() {

    private lateinit var component: ListComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        component =
            ListComponent(
                storeFactory = storeFactory,
                listDataSource = ListDataSourceImpl(app!!.database),
                lifecycle = lifecycle
            )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(ListComponent.LAYOUT_ID, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.bindView(view, viewLifecycleOwner.lifecycle, RedirectHandlerImpl())
    }

    private inner class RedirectHandlerImpl : (ListRedirect) -> Unit {
        override fun invoke(redirect: ListRedirect) {
            when (redirect) {
                is ListRedirect.ShowItemDetails -> router?.startFragment(DetailsFragment.newInstance(redirect.itemId))
            }
        }
    }
}
