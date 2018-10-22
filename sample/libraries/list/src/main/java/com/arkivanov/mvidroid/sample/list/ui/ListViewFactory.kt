package com.arkivanov.mvidroid.sample.list.ui

import android.support.annotation.LayoutRes
import android.view.View
import com.arkivanov.mvidroid.bind.MviViewBundle
import com.arkivanov.mvidroid.bind.using
import com.arkivanov.mvidroid.sample.common.ui.createRedirectViewBundle
import com.arkivanov.mvidroid.sample.list.R
import com.arkivanov.mvidroid.sample.list.component.ListStates
import com.arkivanov.mvidroid.sample.list.component.ListUiEvent
import com.arkivanov.mvidroid.sample.list.model.ListRedirect
import com.arkivanov.mvidroid.sample.list.ui.list.ListView
import com.arkivanov.mvidroid.sample.list.ui.list.ListViewModelMapper

class ListViewFactory(
    private val root: View,
    private val redirectHandler: (ListRedirect) -> Unit
) {

    fun create(): List<MviViewBundle<ListStates, *, ListUiEvent>> =
        listOf(
            ListView(root) using ListViewModelMapper,
            createRedirectViewBundle<ListStates, ListUiEvent, ListRedirect>(
                handler = redirectHandler,
                redirectHandledUiEvent = ListUiEvent.OnRedirectHandled,
                getRedirects = ListStates::redirectStates
            )
        )

    companion object {
        @LayoutRes
        val LAYOUT_ID = R.layout.layout_todo_list
    }
}