package com.arkivanov.mvikotlin.core.lifecycle

interface Lifecycle {

    val state: State

    fun register(callbacks: Callbacks)

    fun unregister(callbacks: Callbacks)

    enum class State {
        INITIALIZED, CREATED, STARTED, RESUMED, DESTROYED;

        companion object {
            val VALUES: List<State> = values().toList()
        }
    }

    interface Callbacks {
        fun onCreate() {
        }

        fun onStart() {
        }

        fun onResume() {
        }

        fun onPause() {
        }

        fun onStop() {
        }

        fun onDestroy() {
        }
    }
}
