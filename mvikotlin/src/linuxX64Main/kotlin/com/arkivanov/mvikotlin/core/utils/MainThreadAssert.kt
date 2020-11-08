@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.arkivanov.mvikotlin.core.utils

fun setMainThreadId(id: ULong) {
    com.arkivanov.mvikotlin.utils.internal.setMainThreadId(id)
}
