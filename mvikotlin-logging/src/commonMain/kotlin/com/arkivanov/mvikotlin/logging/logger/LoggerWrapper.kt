package com.arkivanov.mvikotlin.logging.logger

internal class LoggerWrapper(
    private val logger: Logger,
    private val logFormatter: LogFormatter
) : Logger by logger, LogFormatter by logFormatter
