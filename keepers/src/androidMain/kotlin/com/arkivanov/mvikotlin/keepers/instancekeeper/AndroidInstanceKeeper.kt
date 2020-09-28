package com.arkivanov.mvikotlin.keepers.instancekeeper

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

@ExperimentalInstanceKeeperApi
fun ViewModelStoreOwner.getInstanceKeeper(): InstanceKeeper =
    ViewModelProvider(this)
        .get(InstanceKeeperViewModel::class.java)
        .instanceKeeper
