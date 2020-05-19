package root

import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry

class LifecycleWrapper {

    val lifecycle = LifecycleRegistry()

    init {
        debugLog("lifecycle onCreate")
        lifecycle.onCreate()
    }

    fun start() {
        debugLog("lifecycle onStart")
        lifecycle.onStart()
        debugLog("lifecycle onResume")
        lifecycle.onResume()
    }

    fun stop() {
        debugLog("lifecycle onPause")
        lifecycle.onPause()
        debugLog("lifecycle onStop")
        lifecycle.onStop()
        debugLog("lifecycle onDestroy")
        lifecycle.onDestroy()
    }
}