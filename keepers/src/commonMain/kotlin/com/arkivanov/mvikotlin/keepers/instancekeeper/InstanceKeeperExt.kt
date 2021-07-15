package com.arkivanov.mvikotlin.keepers.instancekeeper

/**
 * Same as [InstanceKeeper.get] but the key is `T::class`
 */
@ExperimentalInstanceKeeperApi
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "This API is now provided by Essenty library: github.com/arkivanov/Essenty")
inline fun <reified T : InstanceKeeper.Instance> InstanceKeeper.get(noinline factory: () -> T): T = get(T::class, factory)
