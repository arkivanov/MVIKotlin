package com.arkivanov.mvikotlin.core.utils

import com.arkivanov.mvikotlin.core.view.ViewRenderer

inline fun <Model> diff(block: DiffBuilder<Model>.() -> Unit): ViewRenderer<Model> {
    val builder =
        object : DiffBuilder<Model>(), ViewRenderer<Model> {
            override fun render(model: Model) {
                binders.forEach { it.render(model) }
            }
        }

    builder.block()

    return builder
}

open class DiffBuilder<Model> {

    @PublishedApi
    internal val binders = ArrayList<ViewRenderer<Model>>()

    inline fun <T> diff(
        crossinline get: (Model) -> T,
        crossinline compare: (new: T, old: T) -> Boolean = { a, b -> a == b },
        crossinline bind: (T) -> Unit
    ) {
        binders +=
            object : ViewRenderer<Model> {
                private var oldValue: T? = null
                private var hasOldValue = false

                override fun render(model: Model) {
                    val newValue = get(model)
                    val oldValue = oldValue
                    this.oldValue = newValue

                    @Suppress("UNCHECKED_CAST")
                    if (!hasOldValue || !compare(newValue, oldValue as T)) {
                        bind(newValue)
                    }
                }
            }
    }
}
