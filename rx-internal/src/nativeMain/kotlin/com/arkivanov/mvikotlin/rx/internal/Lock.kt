package com.arkivanov.mvikotlin.rx.internal

import kotlinx.cinterop.Arena
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import platform.posix.pthread_mutex_destroy
import platform.posix.pthread_mutex_init
import platform.posix.pthread_mutex_lock
import platform.posix.pthread_mutex_t
import platform.posix.pthread_mutex_unlock
import platform.posix.pthread_mutexattr_destroy
import platform.posix.pthread_mutexattr_init
import platform.posix.pthread_mutexattr_settype
import platform.posix.pthread_mutexattr_t
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

@OptIn(ExperimentalForeignApi::class)
actual class Lock actual constructor() {

    private val resources = Resources()

    @Suppress("unused") // Must be assigned
    @OptIn(ExperimentalNativeApi::class)
    private val cleaner = createCleaner(resources, Resources::destroy)

    actual inline fun <T> synchronizedImpl(block: () -> T): T {
        lock()
        try {
            return block()
        } finally {
            unlock()
        }
    }

    fun lock() {
        pthread_mutex_lock(resources.mutex.ptr)
    }

    fun unlock() {
        pthread_mutex_unlock(resources.mutex.ptr)
    }

    private class Resources {
        private val arena = Arena()
        private val attr: pthread_mutexattr_t = arena.alloc()
        val mutex: pthread_mutex_t = arena.alloc()

        init {
            pthread_mutexattr_init(attr.ptr)
            pthread_mutexattr_settype(attr.ptr, PTHREAD_MUTEX_RECURSIVE)
            pthread_mutex_init(mutex.ptr, attr.ptr)
        }

        fun destroy() {
            pthread_mutex_destroy(mutex.ptr)
            pthread_mutexattr_destroy(attr.ptr)
            arena.clear()
        }
    }
}
