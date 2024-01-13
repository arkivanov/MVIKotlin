package com.arkivanov.mvikotlin.timetravel

import java.lang.reflect.TypeVariable

internal actual fun Any.parseType(): String {
    if (javaClass.isArray) {
        return "Array<${javaClass.componentType?.getFixedName() ?: "T"}>"
    }

    return javaClass
        .typeParameters
        .takeUnless(Array<*>::isEmpty)
        ?.joinToString(
            separator = ", ",
            prefix = "${javaClass.getFixedName()}<",
            postfix = ">",
            transform = TypeVariable<*>::getName,
        )
        ?: javaClass.getFixedName()
}

private fun Class<*>.getFixedName(): String =
    this.kotlin.simpleName ?: simpleName
