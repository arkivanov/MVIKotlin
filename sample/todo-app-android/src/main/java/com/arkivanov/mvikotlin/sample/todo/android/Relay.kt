package com.arkivanov.mvikotlin.sample.todo.android

import java.util.concurrent.CopyOnWriteArraySet

class Relay<T> : (T) -> Unit {

    private val consumers = CopyOnWriteArraySet<LifecycledConsumer<T>>()

    fun subscribe(consumer: LifecycledConsumer<T>) {
        consumers += consumer
        consumer.lifecycle.doOnDestroy {
            consumers -= consumer
        }
    }

    override fun invoke(value: T) {
        consumers.forEach { it.input(value) }
    }
}
