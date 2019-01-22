package com.arkivanov.mvidroid.utils

import java.util.*

/**
 * Diffs new model with previous one using provided mappers and comparators
 * and calls appropriate model consumers if changed
 *
 * @param T type of Model
 */
class ModelDiff<T> {

    private val diffs = LinkedList<Diff<T, *>>()
    private var oldModel: T? = null

    /**
     * Registers a diff strategy
     *
     * @param mapper a function that returns a value from model to diff
     * @param comparator custom comparator that compares two values and returns true if they are equal, false otherwise
     * @param consumer a consumer that will be called with a value if it differs from previous one
     * @param S type of value
     */
    fun <S> diff(mapper: (T) -> S, comparator: (newValue: S, oldValue: S) -> Boolean, consumer: (S) -> Unit) {
        diffs.add(Diff(mapper, comparator, consumer))
    }

    /**
     * Accepts a model and diffs it using registered strategies, see [diff] for more information
     */
    fun accept(model: T) {
        val prevModel = oldModel
        oldModel = model
        diffs.forEach { it(model, prevModel) }
    }

    private class Diff<in T, S>(
        private val mapper: (T) -> S,
        private val comparator: (newValue: S, oldValue: S) -> Boolean,
        private val consumer: (S) -> Unit
    ) {
        operator fun invoke(newModel: T, previousModel: T?) {
            val newValue = mapper(newModel)
            if ((previousModel == null) || !comparator(newValue, mapper(previousModel))) {
                consumer(newValue)
            }
        }
    }
}
