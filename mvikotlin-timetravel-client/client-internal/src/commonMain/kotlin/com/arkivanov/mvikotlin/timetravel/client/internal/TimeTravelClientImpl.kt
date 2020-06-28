package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.mappers.eventToAction
import com.arkivanov.mvikotlin.timetravel.client.internal.mappers.eventToIntent
import com.arkivanov.mvikotlin.timetravel.client.internal.mappers.labelToAction
import com.arkivanov.mvikotlin.timetravel.client.internal.mappers.stateToModel
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.mapNotNull

internal class TimeTravelClientImpl(
    connector: TimeTravelClientStoreFactory.Connector,
    view: TimeTravelClientView
) : TimeTravelClient {

    private val store =
        TimeTravelClientStoreFactory(
            storeFactory = DefaultStoreFactory,
            connector = connector
        ).create()

    private val binder =
        bind {
            store.states.map(stateToModel) bindTo view
            store.labels.mapNotNull(labelToAction) bindTo view::execute
            view.events.mapNotNull(eventToIntent) bindTo store
            view.events.mapNotNull(eventToAction) bindTo view::execute
        }

    override fun onCreate() {
        binder.start()
    }

    override fun onDestroy() {
        binder.stop()
        store.dispose()
    }
}
