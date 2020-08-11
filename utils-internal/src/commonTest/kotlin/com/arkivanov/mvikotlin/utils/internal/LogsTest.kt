package com.arkivanov.mvikotlin.utils.internal

import kotlin.test.Test

class LogsTest {

    @Test
    fun prints_logV() {
        logV("Test logV")
    }

    @Test
    fun prints_logE() {
        logE("Test logE")
    }
}
