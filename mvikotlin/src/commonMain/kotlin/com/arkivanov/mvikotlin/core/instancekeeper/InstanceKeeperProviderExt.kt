package com.arkivanov.mvikotlin.core.instancekeeper

/**
 * Same as [InstanceKeeperProvider.get] but the key is T::class
 */
@ExperimentalInstanceKeeperApi
inline fun <reified T : Any> InstanceKeeperProvider.get(): InstanceKeeper<T> = get(T::class)
