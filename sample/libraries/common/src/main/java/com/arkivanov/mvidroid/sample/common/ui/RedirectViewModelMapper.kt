package com.arkivanov.mvidroid.sample.common.ui

import com.arkivanov.mvidroid.bind.MviViewModelMapper
import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectState
import io.reactivex.Observable

internal class RedirectViewModelMapper<in States : Any, Redirect : Any>(
    private val getRedirects: (States) -> Observable<RedirectState<Redirect>>
) : MviViewModelMapper<States, RedirectState<Redirect>> {

    override fun map(states: States): Observable<RedirectState<Redirect>> = getRedirects(states)
}