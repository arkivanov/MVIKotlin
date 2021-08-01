package com.arkivanov.mvikotlin.keepers.instancekeeper

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

@ExperimentalInstanceKeeperApi
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
fun ViewModelStoreOwner.getInstanceKeeper(): InstanceKeeper =
    ViewModelProvider(this)
        .get(InstanceKeeperViewModel::class.java)
        .instanceKeeper
