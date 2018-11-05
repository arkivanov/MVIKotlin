package com.arkivanov.mvidroid.sample.list.component

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import com.arkivanov.mvidroid.sample.list.model.ListRedirect
import com.arkivanov.mvidroid.sample.list.store.list.ListState
import io.reactivex.Observable

class ListStates(
    val listStates: Observable<out ListState>,
    val redirectStates: Observable<out RedirectState<ListRedirect>>
)