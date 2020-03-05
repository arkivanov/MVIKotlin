package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.logging.LoggingMode
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.logger.log

internal class LoggingReducer<State : Any, in Result : Any>(
    private val delegate: Reducer<State, Result>,
    private val logger: Logger,
    private val loggingMode: () -> LoggingMode,
    private val storeName: String
) : Reducer<State, Result> {

    override fun State.reduce(result: Result): State {
        val newState = with(delegate) { reduce(result) }
        logger.log(loggingMode(), storeName, StoreEventType.STATE, newState)

        return newState
    }
}
