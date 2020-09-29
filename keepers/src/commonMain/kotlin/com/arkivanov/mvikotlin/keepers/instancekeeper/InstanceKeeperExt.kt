package com.arkivanov.mvikotlin.keepers.instancekeeper

/**
 * Same as [InstanceKeeper.getOrCreate] but the key is `T::class`
 */
@ExperimentalInstanceKeeperApi
inline fun <reified T : InstanceKeeper.Instance> InstanceKeeper.getOrCreate(noinline factory: () -> T): T =
    getOrCreate(T::class, factory)
