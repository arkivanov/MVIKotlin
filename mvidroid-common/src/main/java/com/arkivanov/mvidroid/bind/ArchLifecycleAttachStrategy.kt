package com.arkivanov.mvidroid.bind

/**
 * Determines when [MviLifecycleObserver.onStart] and [MviLifecycleObserver.onStop] callbacks should be called
 */
enum class ArchLifecycleAttachStrategy {

    /**
     * [MviLifecycleObserver.onStart] and [MviLifecycleObserver.onStop] callbacks will be called from
     * onStart and onStop callbacks respectively
     */
    START_STOP,

    /**
     * [MviLifecycleObserver.onStart] and [MviLifecycleObserver.onStop] callbacks will be called from
     * onResume and onPause callbacks respectively
     */
    RESUME_PAUSE,

    /**
     * [MviLifecycleObserver.onStart] and [MviLifecycleObserver.onStop] callbacks will be called from
     * onCreate and onDestroy callbacks respectively
     */
    CREATE_DESTROY
}
