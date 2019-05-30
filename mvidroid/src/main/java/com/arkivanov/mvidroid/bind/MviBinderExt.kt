package com.arkivanov.mvidroid.bind

import com.arkivanov.mvidroid.view.MviView
import io.reactivex.Observable

/** Adds multiple View Bundles, see [MviViewBundle] for more information
 *
 * @param bundles an Iterable containing all View Bundles
 * @return same instance of MviBinder for method chaining
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Use RxUtils.kt from com.arkivanov.mvidroid.utils package and bind sources to consumers manually")
fun <ComponentEvent : Any, ComponentStates : Any> MviBinder<ComponentEvent, ComponentStates>.addViewBundles(
    bundles: Iterable<MviViewBundle<ComponentStates, ComponentEvent>>
): MviBinder<ComponentEvent, ComponentStates> {
    bundles.forEach { addViewBundle(it) }
    return this
}

/**
 * Adds View and its mappers to MviBinder, see [MviBinder] for more information
 *
 * @param view an instance of [MviView]
 * @param modelMapper a View Model Mapper responsible for mapping of Component States to View Models
 * @param eventMapper a View Event Mapper responsible for mapping of View Events to Component Events
 * @return same instance of MviBinder for method chaining
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Use RxUtils.kt from com.arkivanov.mvidroid.utils package and bind sources to consumers manually")
fun <ComponentEvent : Any, ComponentStates : Any, ViewModel : Any, ViewEvent : Any> MviBinder<ComponentEvent, ComponentStates>.addView(
    view: MviView<ViewModel, ViewEvent>,
    modelMapper: (ComponentStates) -> Observable<out ViewModel>,
    eventMapper: (ViewEvent) -> ComponentEvent?
): MviBinder<ComponentEvent, ComponentStates> =
    addViewBundle(viewBundle(view, modelMapper, eventMapper))

/**
 * Adds View and its View Model Mapper to MviBinder.
 * For views whose Event type is same as Component Event type. See [MviBinder] for more information.
 *
 * @param view an instance of [MviView]
 * @param modelMapper a View Model Mapper responsible for mapping of Component States to View Models
 * @return same instance of MviBinder for method chaining
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Use RxUtils.kt from com.arkivanov.mvidroid.utils package and bind sources to consumers manually")
fun <ComponentEvent : Any, ComponentStates : Any, ViewModel : Any> MviBinder<ComponentEvent, ComponentStates>.addView(
    view: MviView<ViewModel, ComponentEvent>,
    modelMapper: (ComponentStates) -> Observable<out ViewModel>
): MviBinder<ComponentEvent, ComponentStates> =
    addViewBundle(viewBundle(view, modelMapper))

/**
 * Adds View to MviBinder.
 * For views that accept Store state as View Model and whose Event type is same as Component Event type.
 * See [MviBinder] for more information.
 *
 * @param view an instance of [MviView]
 * @return same instance of MviBinder for method chaining
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Use RxUtils.kt from com.arkivanov.mvidroid.utils package and bind sources to consumers manually")
fun <ComponentEvent : Any, StoreState : Any> MviBinder<ComponentEvent, Observable<out StoreState>>.addView(
    view: MviView<StoreState, ComponentEvent>
): MviBinder<ComponentEvent, Observable<out StoreState>> =
    addViewBundle(viewBundle(view))
