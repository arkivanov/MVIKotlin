package com.arkivanov.mvidroid.bind

import android.support.annotation.MainThread

/**
 * Life-cycle observer that provides you control over View and Component life-cycles.
 * See [MviBinder] for more information.
 */
// TODO: To be moved to "utils" package
interface MviLifecycleObserver {

    /**
     * Call this method when View should start receiving View Model updates.
     * Typically called from activity's or fragment's onStart() method.
     * Must be called only from Main thread.
     */
    @MainThread
    fun onStart()

    /**
     * Call this method when View should stop receiving View Model updates.
     * Typically called from activity's or fragment's onStop() method.
     * Must be called only from Main thread.
     */
    @MainThread
    fun onStop()

    /**
     * Call this method when Component should be disposed.
     * Typically called from activity's or fragment's onDestroy() method.
     * Must be called only from Main thread.
     */
    @MainThread
    fun onDestroy()
}
