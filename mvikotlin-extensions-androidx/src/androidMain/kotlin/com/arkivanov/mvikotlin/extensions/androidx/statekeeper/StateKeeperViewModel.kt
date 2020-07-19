package com.arkivanov.mvikotlin.extensions.androidx.statekeeper

import androidx.lifecycle.ViewModel

internal class StateKeeperViewModel : ViewModel() {

    var savedState: MutableMap<String, Any>? = null
}
