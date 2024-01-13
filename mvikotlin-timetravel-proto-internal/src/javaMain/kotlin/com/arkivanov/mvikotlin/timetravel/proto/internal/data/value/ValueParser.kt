package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import java.lang.reflect.TypeVariable

@Suppress("EmptyDefaultConstructor")
actual class ValueParser actual constructor() {

    actual fun parseType(obj: Any): String =
        getTypeName(value = obj)

    private companion object {
        fun getTypeName(value: Any? = null, clazz: Class<*>? = null): String {
            val valueClass = value?.javaClass ?: clazz ?: return "Object"

            if (valueClass.isArray) {
                return "Array<${valueClass.componentType?.getFixedName() ?: "T"}>"
            }

            return valueClass
                .typeParameters
                .takeUnless(Array<*>::isEmpty)
                ?.joinToString(
                    separator = ", ",
                    prefix = "${valueClass.getFixedName()}<",
                    postfix = ">",
                    transform = TypeVariable<*>::getName
                )
                ?: valueClass.getFixedName()
        }

        private fun Class<*>.getFixedName(): String =
            this.kotlin.simpleName ?: simpleName
    }
}
