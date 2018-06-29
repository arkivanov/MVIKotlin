package com.arkivanov.mvidroid.bind

import com.arkivanov.mvidroid.view.MviView

/**
 * Groups View and View Model Mapper together. See [bind] for more information.
 *
 * @param States type of Component's States
 * @param ViewModel type of View Model
 * @param UiEvent type of View's UI Events
 */
class MviViewBundle<in States : Any, ViewModel : Any, UiEvent : Any>(
    val view: MviView<ViewModel, UiEvent>,
    val mapper: MviViewModelMapper<States, ViewModel>
)
