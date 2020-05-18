package root

import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry

class LifecycleWrapper {

    val lifecycle = LifecycleRegistry()

    init {
        lifecycle.onCreate()
    }

    fun start() {
        lifecycle.onStart()
        lifecycle.onResume()
    }

    fun stop() {
        lifecycle.onPause()
        lifecycle.onStop()
        lifecycle.onDestroy()
    }
}