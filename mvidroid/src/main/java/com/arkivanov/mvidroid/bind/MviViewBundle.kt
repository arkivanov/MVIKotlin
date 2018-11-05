package com.arkivanov.mvidroid.bind

import com.arkivanov.mvidroid.view.MviView
import io.reactivex.Observable

/**
 * Groups View and its mappers together. See [MviBinder] for more information.
 */
class MviViewBundle<in ComponentStates : Any, out ComponentEvent : Any> private constructor(
    internal val view: MviView<Any, Any>,
    internal val modelMapper: (ComponentStates) -> Observable<out Any>,
    internal val eventMapper: (Any) -> ComponentEvent?
) {

    companion object {
        fun <ComponentStates : Any, ComponentEvent : Any, ViewModel : Any, ViewEvent : Any> create(
            view: MviView<ViewModel, ViewEvent>,
            modelMapper: (ComponentStates) -> Observable<out ViewModel>,
            eventMapper: (ViewEvent) -> ComponentEvent?
        ): MviViewBundle<ComponentStates, ComponentEvent> =
            @Suppress("UNCHECKED_CAST")
            MviViewBundle(view as MviView<Any, Any>, modelMapper, eventMapper as (Any) -> ComponentEvent?)

        fun <ComponentStates : Any, ComponentEvent : Any, ViewModel : Any> create(
            view: MviView<ViewModel, ComponentEvent>,
            modelMapper: (ComponentStates) -> Observable<out ViewModel>
        ): MviViewBundle<ComponentStates, ComponentEvent> =
            @Suppress("UNCHECKED_CAST")
            MviViewBundle(view as MviView<Any, Any>, modelMapper, { it as ComponentEvent })
    }
}
