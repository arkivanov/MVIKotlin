package com.arkivanov.mvikotlin.core.utils

import com.arkivanov.mvikotlin.core.utils.internal.logE
import com.arkivanov.mvikotlin.core.utils.internal.logV
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
