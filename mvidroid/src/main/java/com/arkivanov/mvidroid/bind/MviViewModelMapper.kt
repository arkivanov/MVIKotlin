package com.arkivanov.mvidroid.bind

import android.support.annotation.MainThread
import io.reactivex.Observable

/**
 * Maps Component's States to View Models
 *
 * @param States type of Component's States
 * @param ViewModel type of View Model
 */
interface MviViewModelMapper<in States : Any, ViewModel : Any> {

    /**
     * Maps Component's States to View Models, called on Main thread
     *
     * @param states States of Component
     * @return Observable of View Model
     */
    @MainThread
    fun map(states: States): Observable<ViewModel>
}
