package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.timetravel.TimeTravelState

internal class StateHolder(
    private var state: TimeTravelState
) {

    private val monitor = Object()

    fun offer(state: TimeTravelState) {
        synchronized(monitor) {
            this.state = state
            monitor.notifyAll()
        }
    }

    @Throws(InterruptedException::class)
    fun getNew(previous: TimeTravelState? = null): TimeTravelState {
        synchronized(monitor) {
            while (true) {
                if (state !== previous) {
                    return state
                }

                monitor.wait()
            }
        }
    }
}
