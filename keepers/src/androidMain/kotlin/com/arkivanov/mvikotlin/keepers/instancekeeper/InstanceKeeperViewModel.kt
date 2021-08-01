package com.arkivanov.mvikotlin.keepers.instancekeeper

import androidx.lifecycle.ViewModel

@ExperimentalInstanceKeeperApi
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
internal class InstanceKeeperViewModel : ViewModel() {

    val instanceKeeper: DefaultInstanceKeeper = DefaultInstanceKeeper()

    override fun onCleared() {
        instanceKeeper.destroy()

        super.onCleared()
    }
}
