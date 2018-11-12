package com.arkivanov.mvidroid.bind

import com.arkivanov.mvidroid.view.MviView
import io.reactivex.Observable

/**
 * Groups View and its mappers together. See [MviBinder] for more information.
 */
class MviViewBundle<in ComponentStates : Any, out ComponentEvent : Any> internal constructor(
    internal val view: MviView<Any, Any>,
    internal val modelMapper: (ComponentStates) -> Observable<out Any>,
    internal val eventMapper: (Any) -> ComponentEvent?
)

fun <ComponentStates : Any, ComponentEvent : Any, ViewModel : Any, ViewEvent : Any> viewBundle(
    view: MviView<ViewModel, ViewEvent>,
    modelMapper: (ComponentStates) -> Observable<out ViewModel>,
    eventMapper: (ViewEvent) -> ComponentEvent?
): MviViewBundle<ComponentStates, ComponentEvent> =
    @Suppress("UNCHECKED_CAST")
    MviViewBundle(view as MviView<Any, Any>, modelMapper, eventMapper as (Any) -> ComponentEvent?)

fun <ComponentStates : Any, ComponentEvent : Any, ViewModel : Any> viewBundle(
    view: MviView<ViewModel, ComponentEvent>,
    modelMapper: (ComponentStates) -> Observable<out ViewModel>
): MviViewBundle<ComponentStates, ComponentEvent> =
    viewBundle(view, modelMapper, { it })

fun <StoreState : Any, ComponentEvent : Any> viewBundle(
    view: MviView<StoreState, ComponentEvent>
): MviViewBundle<Observable<out StoreState>, ComponentEvent> =
    viewBundle(view, { it }, { it })
