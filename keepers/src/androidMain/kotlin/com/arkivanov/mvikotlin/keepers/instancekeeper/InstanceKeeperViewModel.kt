package com.arkivanov.mvikotlin.keepers.instancekeeper

import androidx.lifecycle.ViewModel

@ExperimentalInstanceKeeperApi
internal class InstanceKeeperViewModel : ViewModel() {

    val instanceKeeper = DefaultInstanceKeeper()

    override fun onCleared() {
        instanceKeeper.destroy()

        super.onCleared()
    }
}
