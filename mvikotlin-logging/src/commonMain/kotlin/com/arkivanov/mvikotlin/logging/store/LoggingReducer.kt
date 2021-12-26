package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.logger.LoggerWrapper
import com.arkivanov.mvikotlin.logging.logger.log

internal class LoggingReducer<State : Any, in Message : Any>(
    private val delegate: Reducer<State, Message>,
    private val logger: LoggerWrapper,
    private val storeName: String
) : Reducer<State, Message> {

    override fun State.reduce(msg: Message): State {
        val newState = with(delegate) { reduce(msg) }
        logger.log(storeName = storeName, eventType = StoreEventType.STATE, value = newState)

        return newState
    }
}
