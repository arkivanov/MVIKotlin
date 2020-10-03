package com.arkivanov.mvikotlin.keepers.instancekeeper

/**
 * Same as [InstanceKeeper.get] but the key is `T::class`
 */
@ExperimentalInstanceKeeperApi
inline fun <reified T : InstanceKeeper.Instance> InstanceKeeper.get(noinline factory: () -> T): T = get(T::class, factory)
