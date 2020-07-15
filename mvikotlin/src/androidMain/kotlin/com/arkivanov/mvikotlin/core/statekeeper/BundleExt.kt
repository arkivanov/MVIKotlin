package com.arkivanov.mvikotlin.core.statekeeper

import android.os.Bundle
import kotlin.reflect.KClass

internal inline fun <T : Any> Bundle.putSafe(key: String, value: T, writer: Bundle.(String, T) -> Unit) {
    putBundle(key, Bundle().apply { writer(key, value) })
}

internal inline fun <T : Any> Bundle.getSafe(key: String, clazz: KClass<out T>, reader: Bundle.(String) -> T?): T? =
    getBundle(key)?.withClassLoader(clazz.java.classLoader) { reader(key) }

private inline fun <T> Bundle.withClassLoader(classLoader: ClassLoader?, block: Bundle.() -> T): T =
    if (classLoader == null) {
        block()
    } else {
        val savedClassLoader = this.classLoader
        this.classLoader = classLoader
        try {
            block()
        } finally {
            this.classLoader = savedClassLoader
        }
    }
