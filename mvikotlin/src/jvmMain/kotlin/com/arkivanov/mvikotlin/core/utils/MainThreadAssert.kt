@file:JvmName("MainThreadAssert")

package com.arkivanov.mvikotlin.core.utils

fun setMainThreadId(id: Long) {
    com.arkivanov.mvikotlin.utils.internal.setMainThreadId(id)
}
