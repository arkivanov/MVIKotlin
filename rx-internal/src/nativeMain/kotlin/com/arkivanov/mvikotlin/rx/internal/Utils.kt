package com.arkivanov.mvikotlin.rx.internal

/**
 * Wrapper for platform.posix.PTHREAD_MUTEX_RECURSIVE which
 * is represented as kotlin.Int on darwin platforms and kotlin.UInt on linuxX64
 * See: // https://youtrack.jetbrains.com/issue/KT-41509
 */
internal expect val PTHREAD_MUTEX_RECURSIVE: Int
