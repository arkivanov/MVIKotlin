package com.arkivanov.mvidroid.sample.list.ui

import android.support.annotation.LayoutRes
import android.view.View
import com.arkivanov.mvidroid.bind.MviViewBundle
import com.arkivanov.mvidroid.sample.common.ui.redirect.RedirectView
import com.arkivanov.mvidroid.sample.list.R
import com.arkivanov.mvidroid.sample.list.component.ListEvent
import com.arkivanov.mvidroid.sample.list.component.ListStates
import com.arkivanov.mvidroid.sample.list.model.ListRedirect
import com.arkivanov.mvidroid.sample.list.ui.list.ListView
import com.arkivanov.mvidroid.sample.list.ui.list.ListViewModelMapper

class ListViewFactory(
    private val root: View,
    private val redirectHandler: (ListRedirect) -> Unit
) {

    fun create(): List<MviViewBundle<ListStates, ListEvent>> =
        listOf(
            MviViewBundle.create(ListView(root), ListViewModelMapper),
            MviViewBundle.create(RedirectView(redirectHandler), ListStates::redirectStates) { ListEvent.OnRedirectHandled }
        )

    companion object {
        @LayoutRes
        val LAYOUT_ID = R.layout.layout_todo_list
    }
}