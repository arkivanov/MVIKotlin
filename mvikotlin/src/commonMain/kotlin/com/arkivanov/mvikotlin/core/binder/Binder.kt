package com.arkivanov.mvikotlin.core.binder

/**
 * Controls bindings connecting and disconnecting them in lifecycle callbacks
 */
interface Binder {

    /**
     * Connects all the managed bindings
     */
    fun start()

    /**
     * Disconnects all the managed bindings
     */
    fun stop()
}
