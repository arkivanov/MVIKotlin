package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlin.reflect.KClass

interface ExecutionHandler<in T : Any, in Scope : Any> {

    fun handle(scope: Scope, value: T): Boolean

}


inline fun <reified T : Any, Scope : Any> typedExecutionHandler(
    nestedHandler: ExecutionHandler<T, Scope>,
): ExecutionHandler<T, Scope> {
    return TypeCheckingExecutionHandler(T::class, nestedHandler)
}

class TypeCheckingExecutionHandler<T : Any, Scope : Any>(
    private val type: KClass<T>,
    private val nestedHandler: ExecutionHandler<T, Scope>,
) : ExecutionHandler<Any, Scope> {

    override fun handle(scope: Scope, value: Any): Boolean {
        return if (type.isInstance(value)) {
            @Suppress("UNCHECKED_CAST")
            nestedHandler.handle(scope, value as T)
            return true
        } else {
            false
        }
    }

}

inline fun <reified T : Any, Scope : Any> directExecutionHandler(
    noinline handler: Scope.(T) -> Unit
): ExecutionHandler<T, Scope> {
    return typedExecutionHandler(DirectExecutionHandler(handler))
}


class DirectExecutionHandler<T : Any, Scope : Any>(
    private val nestedHandler: Scope.(T) -> Unit,
) : ExecutionHandler<T, Scope> {

    override fun handle(scope: Scope, value: T): Boolean {
        nestedHandler.invoke(scope, value)
        return true
    }

}

